//  repository for persisting and accessing data for invoice items

package com.demo.project2.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demo.project2.model.InvoiceItem;

@Repository
public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, Long> {

    @Override
    Optional<InvoiceItem> findById(Long id);

}
