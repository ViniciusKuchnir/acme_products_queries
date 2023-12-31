package com.isep.acme.services;

import com.isep.acme.model.Product;
import com.isep.acme.model.ProductDTO;
import com.isep.acme.model.ProductDetailDTO;
import com.isep.acme.model.User;
import com.isep.acme.repositories.ProductRepository;
import com.isep.acme.repositories.UserRepository;

import com.opencsv.*;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

@Service
public class ProductServiceImpl implements ProductService {

    private Set<String> processedSkus = new HashSet<>();
    private String filePath = "product.csv";
    public void create(Product product) throws IOException {
        boolean fileExists = new File(filePath).exists();

        try (CSVWriter csvWriter = new CSVWriter(new FileWriter(filePath, true))) {

            // Caso o arquivo não exista, cria o cabeçalho
            if (!fileExists) {
                String[] header = {"SKU", "Designation", "Description"};
                csvWriter.writeNext(header);
            }

            if (processedSkus.contains(product.getSku())){
                //SKU já existente. Evita duplicações de dados
                return;
            }

            // Escreva o produto no CSV
            String[] row = {product.getSku(), product.getDesignation(), product.getDescription()};
            csvWriter.writeNext(row);

            processedSkus.add(product.getSku());

            // Flush e feche o escritor
            csvWriter.flush();
        }
    }

    @Override
    public ProductDTO updateBySku(String sku, Product product) {
        List<String[]> updatedRecords = new ArrayList<>();
        ProductDTO updatedProductDTO = null;

        // Lê os registros existentes do arquivo CSV
        try (CSVReader csvReader = new CSVReaderBuilder(new FileReader(filePath)).build()) {
            String[] nextRecord;

            // Pula o cabeçalho se existir
            csvReader.readNext();

            while ((nextRecord = csvReader.readNext()) != null) {
                String currentSku = nextRecord[0];

                if (currentSku.equals(sku)) {
                    // Atualiza os dados do produto se encontrar o SKU correspondente
                    nextRecord[1] = product.getDesignation();
                    nextRecord[2] = product.getDescription();

                    updatedProductDTO = new ProductDTO(product.getDesignation(), product.getDescription());
                }

                updatedRecords.add(nextRecord);
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }

        // Escreve os registros atualizados de volta no arquivo CSV
        try (ICSVWriter csvWriter = new CSVWriterBuilder(new FileWriter(filePath, false)).build()) {
            // Escreve o cabeçalho
            csvWriter.writeNext(new String[]{"SKU", "Designation", "Description"});

            // Escreve todos os registros, incluindo os atualizados
            for (String[] record : updatedRecords) {
                csvWriter.writeNext(record);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return updatedProductDTO;
    }
    @Override
    public void deleteBySku(String sku) {
        List<String[]> updatedRecords = new ArrayList<>();

        try (CSVReader csvReader = new CSVReaderBuilder(new FileReader(filePath)).build()) {
            String[] nextRecord;

            // Pula o cabeçalho se existir
            csvReader.readNext();

            while ((nextRecord = csvReader.readNext()) != null) {
                String currentSku = nextRecord[0];

                if (!currentSku.equals(sku)) {
                    // Adiciona apenas os registros que não correspondem ao SKU fornecido
                    updatedRecords.add(nextRecord);
                }
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }

        // Escreve os registros atualizados de volta no arquivo CSV
        try (ICSVWriter csvWriter = new CSVWriterBuilder(new FileWriter(filePath)).build()) {
            csvWriter.writeNext(new String[]{"SKU", "Designation", "Description"});

            for (String[] record : updatedRecords) {
                csvWriter.writeNext(record);
            }
        } catch (IOException e) {
            e.printStackTrace(); // ou lidar com a exceção de outra forma
        }
    }


    @Override
    public Optional<Product> getProductBySku(final String sku) {
        try (CSVReader csvReader = new CSVReaderBuilder(new FileReader(filePath)).build()) {
            String[] nextRecord;

            // Pula a leitura do cabeçalho
            csvReader.readNext();

            while ((nextRecord = csvReader.readNext()) != null) {
                String currentSku = nextRecord[0];

                // Se o SKU atual corresponder ao SKU fornecido, crie e retorne o objeto Product
                if (currentSku.equals(sku)) {
                    String designation = nextRecord[1];
                    String description = nextRecord[2];
                    return Optional.of(new Product(currentSku, designation, description));
                }
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace(); // ou lidar com a exceção de outra forma
        }

        // Retorna Optional.empty() se o SKU não for encontrado
        return Optional.empty();
    }

    @Override
    public Optional<ProductDTO> findBySku(String sku) {
        try (CSVReader csvReader = new CSVReaderBuilder(new FileReader(filePath)).build()) {
            String[] nextRecord;

            // Pule o cabeçalho se existir
            csvReader.readNext();

            while ((nextRecord = csvReader.readNext()) != null) {
                String currentSku = nextRecord[0];

                // Se o SKU atual corresponder ao SKU fornecido, cria e retorne o objeto ProductDTO
                if (currentSku.equals(sku)) {
                    String designation = nextRecord[1];

                    // Cria um objeto ProductDTO com os dados encontrados
                    ProductDTO productDTO = new ProductDTO(currentSku, designation);
                    return Optional.of(productDTO);
                }
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }

        // Retorna Optional.empty() se o SKU não for encontrado
        return Optional.empty();
    }


    @Override
    public Iterable<ProductDTO> findByDesignation(final String designation) {
        List<ProductDTO> matchingProducts = new ArrayList<>();

        try (CSVReader csvReader = new CSVReaderBuilder(new FileReader(filePath)).build()) {
            String[] nextRecord;

            // Pula o cabeçalho se existir
            csvReader.readNext();

            while ((nextRecord = csvReader.readNext()) != null) {
                String currentDesignation = nextRecord[1];

                // Se a designação atual corresponder à designação fornecida, cria e adiciona o objeto ProductDTO à lista
                if (currentDesignation.equals(designation)) {
                    String sku = nextRecord[0];
                    String description = nextRecord[2];

                    ProductDTO productDTO = new ProductDTO(sku, currentDesignation);
                    matchingProducts.add(productDTO);
                }
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }

        return matchingProducts;
    }

    @Override
    public Iterable<ProductDTO> getCatalog() {
        List<ProductDTO> catalog = new ArrayList<>();

        try (CSVReader csvReader = new CSVReaderBuilder(new FileReader(filePath)).build()) {
            String[] nextRecord;

            // Pule o cabeçalho se existir
            csvReader.readNext();

            while ((nextRecord = csvReader.readNext()) != null) {
                String sku = nextRecord[0];
                String designation = nextRecord[1];
                String description = nextRecord[2];

                // Cria um objeto ProductDTO com os dados do CSV e adicione à lista
                ProductDTO productDTO = new ProductDTO(sku, designation);
                catalog.add(productDTO);
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace(); // ou lidar com a exceção de outra forma
        }

        return catalog;
    }

    public ProductDetailDTO getDetails(String sku) {
        try (CSVReader csvReader = new CSVReaderBuilder(new FileReader(filePath)).build()) {
            String[] nextRecord;

            // Pula o cabeçalho
            csvReader.readNext();

            while ((nextRecord = csvReader.readNext()) != null) {
                String currentSku = nextRecord[0];

                // Se o SKU atual corresponder ao SKU fornecido, cria e retorna o objeto ProductDetailDTO
                if (currentSku.equals(sku)) {
                    String designation = nextRecord[1];
                    String description = nextRecord[2];

                    return new ProductDetailDTO(currentSku, designation, description);
                }
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }

        // Retorna null se o SKU não for encontrado
        return null;
    }

}
