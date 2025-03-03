package com.example.order.service;

import com.example.order.entity.Order;
import net.sf.jasperreports.engine.JRException;

import java.io.IOException;

public interface InvoiceService {
    public byte[] generateInvoice(Order request) throws IOException, JRException;
}
