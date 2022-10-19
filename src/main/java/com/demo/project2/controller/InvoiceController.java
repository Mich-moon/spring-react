// Controller receives and handles request after it was filtered by OncePerRequestFilter.
// UserController has accessing protected resource methods with role based validations.

package com.demo.project2.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.time.LocalDate;

import com.demo.project2.model.IStatus;
import com.demo.project2.model.Role;
import com.demo.project2.model.URole;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import com.demo.project2.model.Invoice;
import com.demo.project2.repository.InvoiceRepository;

import javax.validation.Valid;


@CrossOrigin(origins = "http://localhost:3000") // to avoid CORS issues:
@RestController
@RequestMapping("api/invoices")
public class InvoiceController {

    @Autowired
    private InvoiceRepository invoiceRepository;


    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'MODERATOR')")
    public ResponseEntity<?>  createInvoice(@Valid @RequestBody Invoice invoiceDetails) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();  // for holding response details

        try {
            // Create new invoice
            Invoice invoice = new Invoice(
                    invoiceDetails.getCompanyFrom(),
                    invoiceDetails.getStreetFrom(),
                    invoiceDetails.getCityFrom(),
                    invoiceDetails.getStateFrom(),
                    invoiceDetails.getZipFrom(),
                    invoiceDetails.getPhoneFrom(),
                    invoiceDetails.getNameTo(),
                    invoiceDetails.getCompanyTo(),
                    invoiceDetails.getStreetTo(),
                    invoiceDetails.getCityTo(),
                    invoiceDetails.getStateTo(),
                    invoiceDetails.getZipTo(),
                    invoiceDetails.getPhoneTo(),
                    invoiceDetails.getEmailTo(),
                    invoiceDetails.getItems(),
                    invoiceDetails.getComments(),
                    invoiceDetails.getCreatedBy(),
                    invoiceDetails.getSubtotal().toString(),
                    invoiceDetails.getTaxRate().toString(),
                    invoiceDetails.getTax().toString(),
                    invoiceDetails.getTotalDue().toString()

            );

            //save Invoice to database using UserRepository
            invoiceRepository.save(invoice);
            map.put("message", "Invoice created successfully!" );
            map.put("invoice", invoice);
            return new ResponseEntity<>(map, HttpStatus.CREATED);

        } catch (Exception ex) {    // creation unsuccessful
            map.clear();
            map.put("message", ex.toString() );
            //map.put("message", "Oops, something went wrong" );
            return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'MODERATOR')")
    public ResponseEntity<?> getInvoices() {
        Map<String, Object> map = new LinkedHashMap<String, Object>();  // for holding response details

        //Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        try {
            List<Invoice> invoiceList = invoiceRepository.findAll();

            /*List<Invoice> invoiceList = new ArrayList<>();

            if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("MODERATOR"))) {
                invoiceList = invoiceRepository.findAll();
            } else {
                invoiceList = invoiceRepository.findAll();
            }*/

            if (!invoiceList.isEmpty()) {  // Invoices found
                map.put("invoices", invoiceList);
                return new ResponseEntity<>(map, HttpStatus.OK);

            } else {    // No invoices found
                map.clear();
                map.put("message", "No invoices found");
                return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
            }

        } catch (Exception ex) {    // Exception
            map.clear();
            map.put("message", "Oops! something went wrong");
            return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'MODERATOR')")
    public ResponseEntity<?>  getInvoice(@PathVariable Long id) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();  // for holding response details

        try {
            if (invoiceRepository.findById(id).isPresent()) {  // Invoice found
                Invoice invoice = invoiceRepository.findById(id).get();  // get value found
                map.put("invoice", invoice);
                return new ResponseEntity<>(map, HttpStatus.OK);

            } else {    // Invoice not found
                map.clear();
                map.put("message", "Invoice not found");
                return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
            }

        } catch (Exception ex) {    // Exception
            map.clear();
            map.put("message", "Oops! something went wrong");
            return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<?> deleteInvoice(@PathVariable Long id) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();  // for holding response details

        try {
            invoiceRepository.deleteById(id);
            map.put("message", "Invoice deleted successfully");
            return new ResponseEntity<>(map, HttpStatus.OK);

        } catch (Exception ex) {    // Exception
            map.clear();
            map.put("message", ex.toString() );
            //map.put("message", "Oops! something went wrong");
            return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'MODERATOR')")
    public ResponseEntity<?> updateInvoice(@RequestBody Invoice invoiceUpdate, @PathVariable Long id) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();  // for holding response details

        try {
            if (invoiceRepository.findById(id).isPresent()) {  // Invoice found
                Invoice currentInvoice = invoiceRepository.findById(id).get(); // get value found

                // updating invoice details - except status
                currentInvoice.setDate(LocalDate.now()); // Date object - (year, month, day (yyyy-MM-dd))
                currentInvoice.setCompanyFrom(invoiceUpdate.getCompanyFrom());
                currentInvoice.setStreetFrom(invoiceUpdate.getStreetFrom());
                currentInvoice.setCityFrom(invoiceUpdate.getCityFrom());
                currentInvoice.setStateFrom(invoiceUpdate.getStateFrom());
                currentInvoice.setZipFrom(invoiceUpdate.getZipFrom());
                currentInvoice.setPhoneFrom(invoiceUpdate.getPhoneFrom());
                currentInvoice.setNameTo(invoiceUpdate.getNameTo());
                currentInvoice.setCompanyTo(invoiceUpdate.getCompanyTo());
                currentInvoice.setStreetTo(invoiceUpdate.getStreetTo());
                currentInvoice.setCityTo(invoiceUpdate.getCityTo());
                currentInvoice.setStateTo(invoiceUpdate.getStateTo());
                currentInvoice.setZipTo(invoiceUpdate.getZipTo());
                currentInvoice.setPhoneTo(invoiceUpdate.getPhoneTo());
                currentInvoice.setEmailTo(invoiceUpdate.getEmailTo());
                currentInvoice.setComments(invoiceUpdate.getComments());
                currentInvoice.setCreatedBy(invoiceUpdate.getCreatedBy());
                currentInvoice.setSubtotal(invoiceUpdate.getSubtotal());
                currentInvoice.setTaxRate(invoiceUpdate.getTaxRate());
                currentInvoice.setTax(invoiceUpdate.getTax());
                currentInvoice.setTotalDue(invoiceUpdate.getTotalDue());


                // NB modify the invoice_items that Hibernate is tracking
                currentInvoice.getItems().clear();
                currentInvoice.getItems().addAll(invoiceUpdate.getItems());

                invoiceRepository.save(currentInvoice);   // save new details

                map.put("invoice", currentInvoice);
                map.put("message", "Invoice updated successfully");
                return new ResponseEntity<>(map, HttpStatus.OK);

            } else {    // User not found
                map.clear();
                map.put("message", "Invoice not found");
                return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
            }

        } catch (Exception ex) {    // Exception
            map.clear();
            map.put("message", ex.toString() );
            //map.put("message", "Oops! something went wrong");
            return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PutMapping("/{id}/update-status")
    @PreAuthorize("hasAnyRole('USER', 'MODERATOR')")
    public ResponseEntity<?> updateInvoice(@PathVariable Long id, @RequestParam String status) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();  // for holding response details

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        try {
            if (invoiceRepository.findById(id).isPresent()) {  // Invoice found
                Invoice currentInvoice = invoiceRepository.findById(id).get(); // get value found

                // updating invoice status
                if (!status.isEmpty()) {

                    switch (status) {
                        case "pending":
                            currentInvoice.setStatus(IStatus.PENDING);
                            break;

                        case "approved":
                            if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("MODERATOR"))) {
                                currentInvoice.setStatus(IStatus.APPROVED);
                            }
                            break;

                        case "paid":
                            if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("MODERATOR"))) {
                                currentInvoice.setStatus(IStatus.PAID);
                            }
                            break;

                        default:
                            currentInvoice.setStatus(IStatus.DRAFT);
                    }
                }

                invoiceRepository.save(currentInvoice);   // save new details

                map.put("invoice", currentInvoice);
                map.put("message", "Invoice updated successfully");
                return new ResponseEntity<>(map, HttpStatus.OK);

            } else {    // User not found
                map.clear();
                map.put("message", "Invoice not found");
                return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
            }

        } catch (Exception ex) {    // Exception
            map.clear();
            map.put("message", "Oops! something went wrong");
            return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
