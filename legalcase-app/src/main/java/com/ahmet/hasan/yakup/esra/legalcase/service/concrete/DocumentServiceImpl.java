package com.ahmet.hasan.yakup.esra.legalcase.service.concrete;

import com.ahmet.hasan.yakup.esra.legalcase.model.Case;
import com.ahmet.hasan.yakup.esra.legalcase.model.Document;
import com.ahmet.hasan.yakup.esra.legalcase.model.enums.DocumentType;
import com.ahmet.hasan.yakup.esra.legalcase.repository.CaseRepository;
import com.ahmet.hasan.yakup.esra.legalcase.repository.DocumentRepository;
import com.ahmet.hasan.yakup.esra.legalcase.service.virtual.DocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class DocumentServiceImpl implements DocumentService {

    private static final Logger logger = LoggerFactory.getLogger(DocumentServiceImpl.class);
    private final String uploadDir = "uploads/documents";

    private final DocumentRepository documentRepository;
    private final CaseRepository caseRepository;

    @Autowired
    public DocumentServiceImpl(DocumentRepository documentRepository, CaseRepository caseRepository) {
        this.documentRepository = documentRepository;
        this.caseRepository = caseRepository;

        // Yükleme dizinini oluştur
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    @Override
    public Document createDocument(Document document) {
        logger.info("Creating new document: {}", document.getTitle());
        return documentRepository.save(document);
    }

    @Override
    public Document uploadDocument(Long caseId, String title, DocumentType type, MultipartFile file) throws IOException {
        logger.info("Uploading new document for case ID: {}", caseId);

        // Case'i kontrol et
        Optional<Case> caseOptional = caseRepository.findById(caseId);
        if (caseOptional.isEmpty()) {
            throw new IllegalArgumentException("Case not found with ID: " + caseId);
        }

        // Dosya adını temizle ve benzersiz hale getir
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String uniqueFilename = UUID.randomUUID().toString() + "_" + originalFilename;

        // Dosyayı diske kaydet
        Path targetLocation = Paths.get(uploadDir).resolve(uniqueFilename);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        // Document nesnesini oluştur
        Document document = new Document();
        document.setTitle(title);
        document.setType(type);
        document.setCse(caseOptional.get());
        document.setFilePath(targetLocation.toString());
        document.setContentType(file.getContentType());
        document.setFileSize(file.getSize());

        return documentRepository.save(document);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Document> getDocumentById(Long id) {
        logger.info("Getting document by ID: {}", id);
        return documentRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Document> getAllDocuments() {
        logger.info("Getting all documents");
        return documentRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Document> getDocumentsByCaseId(Long caseId) {
        logger.info("Getting documents by case ID: {}", caseId);
        return documentRepository.findByCseId(caseId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Document> getDocumentsByType(DocumentType type) {
        logger.info("Getting documents by type: {}", type);
        return documentRepository.findByType(type);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Document> searchDocumentsByTitle(String keyword) {
        logger.info("Searching documents by title containing: {}", keyword);
        return documentRepository.findByTitleContainingIgnoreCase(keyword);
    }

    @Override
    public Document updateDocument(Long id, Document document) {
        logger.info("Updating document with ID: {}", id);

        Optional<Document> existingDocument = documentRepository.findById(id);
        if (existingDocument.isEmpty()) {
            throw new IllegalArgumentException("Document not found with ID: " + id);
        }

        Document documentToUpdate = existingDocument.get();
        documentToUpdate.setTitle(document.getTitle());
        documentToUpdate.setType(document.getType());

        // Dosya içeriği güncellenmiyorsa diğer alanları koru
        if (document.getCse() != null) {
            documentToUpdate.setCse(document.getCse());
        }

        return documentRepository.save(documentToUpdate);
    }

    @Override
    public void deleteDocument(Long id) {
        logger.info("Deleting document with ID: {}", id);

        Optional<Document> document = documentRepository.findById(id);
        if (document.isPresent()) {
            // Dosyayı diskten silme
            try {
                String filePath = document.get().getFilePath();
                if (filePath != null) {
                    Path path = Paths.get(filePath);
                    Files.deleteIfExists(path);
                }
            } catch (IOException e) {
                logger.error("Error deleting document file: {}", e.getMessage(), e);
            }

            // Veritabanından silme
            documentRepository.deleteById(id);
        }
    }

    @Override
    public byte[] getDocumentContent(Long id) throws IOException {
        logger.info("Getting document content for ID: {}", id);

        Optional<Document> document = documentRepository.findById(id);
        if (document.isEmpty() || document.get().getFilePath() == null) {
            throw new IllegalArgumentException("Document not found or file path is missing");
        }

        Path path = Paths.get(document.get().getFilePath());
        return Files.readAllBytes(path);
    }
}