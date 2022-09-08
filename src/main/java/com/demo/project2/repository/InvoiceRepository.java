//  repository for persisting and accessing data for invoices

package com.demo.project2.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demo.project2.model.Invoice;


@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    @Override
    Optional<Invoice> findById(Long id);

}
