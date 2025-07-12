package com.sarasvan.billing.mapper;

import com.sarasvan.billing.entity.InvoiceDetailsEntity;
import com.sarasvan.billing.model.InvoiceDetailsDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import java.util.List;

@Mapper
public interface InvoiceDetailsMapper {
    InvoiceDetailsMapper INSTANCE = Mappers.getMapper(InvoiceDetailsMapper.class);
    InvoiceDetailsEntity dtoToEntity(InvoiceDetailsDTO invoiceDetailsDTO);
    InvoiceDetailsDTO entityToDto(InvoiceDetailsEntity invoiceDetails);

    List<InvoiceDetailsEntity> dtoListToEntityList(List<InvoiceDetailsDTO> dtoList);
    List<InvoiceDetailsDTO> entityListToDtoList(List<InvoiceDetailsEntity> entityList);

}
