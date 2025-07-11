package com.sarasvan.billing.mapper;

import com.sarasvan.billing.entity.InvoiceDetailsEntity;
import com.sarasvan.billing.model.InvoiceDetails;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import java.util.List;

@Mapper
public interface InvoiceDetailsMapper {
    InvoiceDetailsMapper INSTANCE = Mappers.getMapper(InvoiceDetailsMapper.class);
    InvoiceDetailsEntity dtoToEntity(InvoiceDetails invoiceDetails);
    InvoiceDetails entityToDto(InvoiceDetailsEntity invoiceDetails);

    List<InvoiceDetailsEntity> dtoListToEntityList(List<InvoiceDetails> dtoList);
    List<InvoiceDetails> entityListToDtoList(List<InvoiceDetailsEntity> entityList);

}
