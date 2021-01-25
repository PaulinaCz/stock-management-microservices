package com.czerniecka.invoice.service;

import com.czerniecka.invoice.dto.InvoiceDTO;
import com.czerniecka.invoice.dto.InvoiceMapper;
import com.czerniecka.invoice.entity.Invoice;
import com.czerniecka.invoice.repository.InvoiceRepository;
import com.czerniecka.invoice.vo.Product;
import com.czerniecka.invoice.vo.ResponseTemplateVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class InvoiceService {
    
    private final InvoiceRepository invoiceRepository;
    private final InvoiceMapper invoiceMapper;
    private RestTemplate restTemplate;

    @Autowired
    public InvoiceService(InvoiceRepository invoiceRepository, InvoiceMapper invoiceMapper, RestTemplate restTemplate) {
        this.invoiceRepository = invoiceRepository;
        this.invoiceMapper = invoiceMapper;
        this.restTemplate = restTemplate;
    }

    public List<InvoiceDTO> findAll() {

        List<Invoice> all = invoiceRepository.findAll();
        return invoiceMapper.toInvoiceDTOs(all);
    }

    public Optional<ResponseTemplateVO> getInvoiceWithProduct(UUID invoiceId) {
        ResponseTemplateVO vo = new ResponseTemplateVO();
        
        Optional<Invoice> i = invoiceRepository.findById(invoiceId);

        if(i.isPresent()){
            Invoice invoice = i.get();
            Product product = restTemplate.getForObject("http://localhost:3001/products/" + invoice.getProductId(),
                    Product.class);

            product.setId(invoice.getProductId());
            vo.setInvoice(invoiceMapper.toInvoiceDTO(invoice));
            vo.setProduct(product);

            return Optional.of(vo);
        }else{
            return Optional.empty();
        }
    }

    public InvoiceDTO save(InvoiceDTO invoiceDTO) {
        Invoice invoice = invoiceMapper.toInvoice(invoiceDTO);
        return invoiceMapper.toInvoiceDTO(invoiceRepository.save(invoice));
    }
}
