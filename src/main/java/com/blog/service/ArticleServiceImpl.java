package com.blog.service;

import com.blog.converter.ArticleMapper;
import com.blog.dto.ArticleSaveDTO;
import com.blog.exception.*;
import com.blog.model.Article;
import com.blog.model.Author;
import com.blog.repository.ArticleRepository;
import com.blog.repository.AuthorRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@AllArgsConstructor
@Slf4j
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;
    private final ArticleMapper articleMapper;
    private final AuthorRepository authorRepository;


/*    @Override
    public Long save(ArticleSaveDTO article) {
        if(articleRepository.existsByTitle(article.getTitle()))
            throw new IllegalArgumentException("Article: "+article.getTitle()+" already exists");

        if(article.getContent().length() < 5 || article.getContent().length() > 10 )
            throw new IllegalArgumentException("Content has to be between 5 and 10 characters");

        return
                articleRepository.save(
                        articleMapper.toEntity(
                                article)).getId();

    }*/

    @Override
    public Long save(ArticleSaveDTO article) {
        log.info("Attempting to save article with title {}", article.getTitle());
        if(articleRepository.existsByTitle(article.getTitle())) {
            log.warn("Article with title {} already exists", article.getTitle());
            throw new ArticleDuplicatedTitleException(article.getTitle());
        }

        if(article.getContent().length() < 5 || article.getContent().length() > 10 ) {
            log.warn("Article content length invalid for title {}", article.getTitle());
            throw new ArticleContentLengthException();
        }

        if(article.getAuthorId() == null) {
            log.warn("Author is required when saving article {}", article.getTitle());
            throw new ArticleRequiredAuthorException();
        }

        Author author = authorRepository.findById(article.getAuthorId())
                .orElseThrow(() -> new AuthorNotFoundException(article.getAuthorId()));

        Article savedArticle = articleMapper.toEntity(article);
        savedArticle.setAuthor(author);
        Long id = articleRepository.save(savedArticle).getId();
        log.info("Article {} saved successfully with id {}", article.getTitle(), id);
        return id;

    }

    @Transactional
    @Override
    public ArticleSaveDTO update(Long id, ArticleSaveDTO articleDTO) {

        log.info("Attempting to update article {}", id);
        Optional<Article> article = articleRepository.findById(id);

        if(article.isPresent()) {
            Article art = article.get();

            if(articleDTO.getContent().length() < 5 || articleDTO.getContent().length() > 10 ) {
                log.warn("Article content length invalid for update on article {}", id);
                throw new ArticleContentLengthException();
            }

            articleMapper.updateArticleFromDTO(art, articleDTO);
            log.info("Article {} updated successfully", id);
            return articleMapper.toDTO(art);
        }
        log.warn("Article {} not found for update", id);
        throw new ArticleNotFoundException(id);
    }

/*    @Override
    public ArticleSaveDTO update(Long id, ArticleSaveDTO articleDTO) {

        Optional<Article> article = articleRepository.findById(id);

        if(article.isPresent()) {
            Article art = article.get();

            if(art.getContent().length() < 5 || art.getContent().length() > 10 )
                throw new WrongThreadException("Content has to be between 5 and 10 characters");

            articleMapper.updateArticleFromDTO(art, articleDTO);
            return articleMapper.toDTO(articleRepository.save(art));
        }
        throw new IllegalArgumentException("Article not found with id: "+id);
    }*/

    @Override
    public String active(Long id, Boolean active) {
        log.info("Updating active state for article {} to {}", id, active);
        Optional<Article> article = articleRepository.findById(id);
        if(article.isPresent()) {
            Article art = article.get();
            if(art.getIsActive().equals(active)) {
                log.warn("Active state for article {} already {}", id, active);
                throw new ArticleStateAlreadySetException(id, active);
            }
            art.setIsActive(active);
            articleRepository.save(art);
            log.info("Active state for article {} updated to {}", id, active);
            return "Article " + (active ? "activated" : "deactivated") + " successfully.";
        }
        log.warn("Article {} not found when updating active state", id);
        throw new ArticleNotFoundException(id);
    }

    @Override
    public ArticleSaveDTO findById(Long id) {
        log.info("Fetching article {}", id);
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ArticleNotFoundException(id));
        log.info("Article {} fetched successfully", id);
        return articleMapper.toDTO(article);


/*        Optional<Article> article = articleRepository.findById(id);
        if(article.isPresent()) {
            return article.get();
        }
        throw new IllegalArgumentException("Article not found with id: "+id);*/
    }

    @Override
    public List<ArticleSaveDTO> findAll() {
        log.info("Fetching all articles");
        List<Article> articles = articleRepository.findAll();
        log.info("Retrieved {} articles", articles.size());
        return articles.stream().map(articleMapper::toDTO).toList();
    }

    @Override
    public Page<ArticleSaveDTO> findAllPagination(Pageable pageable) {
        log.info("Fetching paginated articles page={} size={}", pageable.getPageNumber(), pageable.getPageSize());
        return articleRepository.findAll(pageable).map(articleMapper::toDTO);
    }

    @Override
    public Page<ArticleSaveDTO> findAllPaginationWithSearch(String criteria, Pageable pageable) {
        log.info("Fetching paginated articles with search criteria={} page={} size={}", criteria, pageable.getPageNumber(), pageable.getPageSize());
        return articleRepository.findAllWithSearch(criteria, pageable).map(articleMapper::toDTO);
    }

    @Override
    public ArticleSaveDTO assignAuthor(Long articleId, Long authorId) {
        log.info("Assigning author {} to article {}", authorId, articleId);
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ArticleNotFoundException(articleId));

        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new AuthorNotFoundException(authorId));

        if (article.getAuthor() != null && authorId.equals(article.getAuthor().getId())) {
            log.warn("Author {} already assigned to article {}", authorId, articleId);
            throw new ArticleAuthorAlreadyAssignedException(articleId, authorId);
        }

        article.setAuthor(author);
        ArticleSaveDTO dto = articleMapper.toDTO(articleRepository.save(article));
        log.info("Author {} assigned to article {}", authorId, articleId);
        return dto;

    }

/*    @Override
    public Page<ArticleSaveDTO> findAllPaginationWithSearch(String criteria, Pageable pageable) {
        List<Article> articles = articleRepository.findAll();

        if(criteria == null) return findAllPagination(pageable);

        List<ArticleSaveDTO> articleSaveDTOS = articles.stream().filter(article -> {

             if(criteria.matches("\\d+")) {
                 return article.getId() != null && article.getId().equals(Long.parseLong(criteria));
             }

             return (article.getTitle() != null && article.getTitle().toLowerCase().contains(criteria.toLowerCase()) ||
                     article.getContent() != null && article.getContent().toLowerCase().contains(criteria.toLowerCase()) ||
                     article.getAuthor() != null && article.getAuthor().toLowerCase().contains(criteria.toLowerCase()));
         })
                .map(articleMapper::toDTO)
                .toList();

        return new PageImpl<>(articleSaveDTOS, pageable, articleSaveDTOS.size());
    }*/


/*    @Override
    public Page<ArticleSaveDTO> findAllPagination(int page, int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        List<ArticleSaveDTO> articleSaveDTOS = findAll();
        return new PageImpl<>(articleSaveDTOS, pageable, articleSaveDTOS.size());
    }*/

/*    @Override
    public ArticleSaveDTO update(Long id, ArticleSaveDTO articleDTO) {

        Optional<Article> article = articleRepository.findById(id);

        if(article.isPresent()) {
            Article art = article.get();

            if(art.getContent().length() < 5 || art.getContent().length() > 10 )
                throw new WrongThreadException("Content has to be between 5 and 10 characters");

            art.setTitle(articleDTO.getTitle());
            art.setContent(articleDTO.getContent());
            art.setAuthor(articleDTO.getAuthor());
            return articleMapper.toDTO(articleRepository.save(art));
        }
        throw new IllegalArgumentException("Article not found with id: "+id);
    }*/

/*    @Override
    public ArticleSaveDTO save(ArticleSaveDTO article) {
        if(articleRepository.existsByTitle(article.getTitle()))
            throw new IllegalArgumentException("Article: "+article.getTitle()+" already exists");

        if(article.getContent().length() < 5 || article.getContent().length() > 10 )
            throw new IllegalArgumentException("Content has to be between 5 and 10 characters");

        return articleMapper.toDTO(
                articleRepository.save(
                        articleMapper.toEntity(
                                article)
                )
        );
    }*/


    /*---Méthodes Helper---*/

/*    Article toEntity(ArticleSaveDTO article){
        Article toBeSaved = new Article();

        toBeSaved.setTitle(article.getTitle());
        toBeSaved.setContent(article.getContent());
        toBeSaved.setAuthor(article.getAuthor());
        return toBeSaved;
    }*/



/*    List<Article> articles = new ArrayList<>(Arrays.asList(
            new Article(1L, new Date(), null,  "Spring Boot Basics", "Learn how to build REST APIs with Spring Boot.", "Taha Arar", true),
            new Article(2L, new Date(), null, "Understanding Dependency Injection", "Explaining DI and its role in Spring.", "John Doe", false),
            new Article(3L, new Date(), null, "Spring Boot Security", "Secure your REST APIs with JWT and cookies.", "Jane Smith", true),
            new Article(4L,  new Date(), null,"Spring Data JPA", "Simplify persistence layer with JPA repositories.", "Ali Ben Salem", false)
    ));

//        List<Article> articles = new ArrayList<>();

    public ArticleServiceImpl() {
    }

    @Override
    public List<Article> findAll() {
        return articles;
    }

    @Override
    public Article findById(Long id) {
        return articles.stream().filter(article -> article.getId().equals(id)).findFirst().orElseThrow(() -> new IllegalArgumentException("Article not found"));
    }

    @Override
    public Long save(ArticleSaveDTO article) {
        System.out.println("From Service: "+article.toString());

//        Long id = null;
//        if(articles.isEmpty()){
//            id = 1L;
//        } else {
//            id = articles.get(articles.size()-1).getId() +1;
//        }
//        String title = article.getTitle();
//        String content = article.getContent();
//        String author = article.getAuthor();

        Long id = articles.isEmpty() ? 1L : articles.getLast().getId() + 1;
        Article savedArticle = new Article(id,
                new Date(),
                null,
                article.getTitle(),
                article.getContent(),
                article.getAuthor());
        System.out.println("Saved Article: "+savedArticle.toString());


        articles.add(savedArticle);
        return savedArticle.getId();
    }

    @Override
    public Article update(Long id, ArticleSaveDTO article) {
        Article existingArticle = findById(id);

        existingArticle.setTitle(article.getTitle());
        existingArticle.setContent(article.getContent());
        existingArticle.setAuthor(article.getAuthor());
        existingArticle.setUpdatedAt(new Date());

        return existingArticle;
    }

    @Override
    public String active(Long id, Boolean active) {
        Article existingArticle = findById(id);
        if(existingArticle.getIsActive().equals(active)) return "Article already " + (active ? "active" : "deactivated");
        existingArticle.setIsActive(active);
        return active ? "Article activated successfully." : "Article deactivated successfully.";
    }

    @Override
    public void delete(Long id) {
        Article existingArticle = findById(id);
        articles.remove(existingArticle);
    }*/

}
