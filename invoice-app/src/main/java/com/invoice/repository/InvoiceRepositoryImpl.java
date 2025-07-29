package com.invoice.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.invoice.model.Invoice;

@Repository
public class InvoiceRepositoryImpl implements InvoiceRepositoryCustom {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public int getNextInvoiceId() {
        Query query = new Query()
                .with(Sort.by(Sort.Direction.DESC, "id"))
                .limit(1);
        Invoice invoice = mongoTemplate.findOne(query, Invoice.class);
        return (invoice != null ? invoice.getId() : 0) + 1;
    }
}
