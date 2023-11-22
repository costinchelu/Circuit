package ro.ase.costin.ecomback.admin;

import lombok.NonNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ro.ase.costin.ecomback.paging.PagingAndSortingArgumentResolver;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        exposeDirectory("user-photos", registry);
        exposeDirectory("category-images", registry);
        exposeDirectory("brand-logos", registry);
        exposeDirectory("product-images", registry);
        exposeDirectory("site-logo", registry);
    }

    private void exposeDirectory(String folderName, ResourceHandlerRegistry registry) {
        Path photosDir = Paths.get(folderName);
        String absolutePathToPhotos = photosDir.toFile().getAbsolutePath();
        String logicalPath = "/" + folderName + "/**";
        String resourceLocation = "file:/" + absolutePathToPhotos + "/";
        registry.addResourceHandler(logicalPath).addResourceLocations(resourceLocation);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new PagingAndSortingArgumentResolver());
    }
}

