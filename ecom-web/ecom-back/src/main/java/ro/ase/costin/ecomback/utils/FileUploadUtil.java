package ro.ase.costin.ecomback.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUploadUtil {

    public static void saveFile(String uploadDir, String fileName, MultipartFile multipartFile) throws IOException {
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        try (InputStream inputStream = multipartFile.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new IOException("Fișierul nu a putut fi salvat: " + fileName, ex);
        }
    }

    public static void cleanDir(String dir) {
        Path dirPath = Paths.get(dir);

        try {
            Files.list(dirPath).forEach(file -> {
                if (!Files.isDirectory(file)) {
                    try {
                        Files.delete(file);
                    } catch (IOException ex) {
                        log.error("Fișierul nu a putut fi șters: " + file);
                    }
                }
            });
        } catch (IOException ex) {
            log.warn("Directorul: " + dirPath + " nu a putut fi citit sau este gol.");
        }
    }

    public static void removeDir(String dir) {
        cleanDir(dir);

        try {
            if (Files.isDirectory(Paths.get(dir))) {
                Files.delete(Paths.get(dir));
            }
        } catch (IOException e) {
            log.error("Directorul: " + dir + " nu a fost șters.");
        }
    }
}
