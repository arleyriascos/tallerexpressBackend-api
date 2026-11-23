package com.tallerexpress.service;

import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.tallerexpress.model.Contador;

/** Genera números consecutivos para las órdenes (OT-0001, OT-0002, ...). */
@Service
public class NumeracionService {

    private final MongoTemplate mongo;

    public NumeracionService(MongoTemplate mongo) {
        this.mongo = mongo;
    }

    public String siguienteNumeroOrden() {
        Contador c = mongo.findAndModify(
                Query.query(Criteria.where("_id").is("orden")),
                new Update().inc("valor", 1),
                FindAndModifyOptions.options().returnNew(true).upsert(true),
                Contador.class);
        return String.format("OT-%04d", c.getValor());
    }
}
