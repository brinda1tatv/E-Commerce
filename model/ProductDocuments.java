package com.eCommerce.model;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Data
@Table(name = "product_documents")
public class ProductDocuments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @OneToOne
    @JoinColumn(name = "product_id")
    private Product productId;

    @NotNull
    @NotBlank
    private String images;

    @NotNull
    @NotBlank
    @Column(name = "warranty_doc")
    private String warrantyDoc;

}
