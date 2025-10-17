package com.blog.service;

import com.blog.converter.ArticleMapper;
import com.blog.dto.ArticleSaveDTO;
import com.blog.model.Article;
import com.blog.repository.ArticleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@AllArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;
    private final ArticleMapper articleMapper;


        @Override
    public Long save(ArticleSaveDTO article) {
        if(articleRepository.existsByTitle(article.getTitle()))
            throw new IllegalArgumentException("Article: "+article.getTitle()+" already exists");

        if(article.getContent().length() < 5 || article.getContent().length() > 10 )
            throw new IllegalArgumentException("Content has to be between 5 and 10 characters");

        return
                articleRepository.save(
                        articleMapper.toEntity(
                                article)).getId();

    }

    @Override
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
    }

    @Override
    public String active(Long id, Boolean active) {
        Optional<Article> article = articleRepository.findById(id);
        if(article.isPresent()) {
            Article art = article.get();
            if(art.getIsActive().equals(active)) return "Article already " + (active ? "active" : "deactivated");
            art.setIsActive(active);
            articleRepository.save(art);
            return "Article " + (active ? "activated" : "deactivated") + " successfully.";
        }
        throw new IllegalArgumentException("Article not found with id: "+id);
    }

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
