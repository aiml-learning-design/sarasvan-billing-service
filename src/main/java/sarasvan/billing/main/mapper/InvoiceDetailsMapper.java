package sarasvan.billing.main.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import sarasvan.billing.main.entity.BusinessDetailsEntity;
import sarasvan.billing.main.entity.InvoiceDetailsEntity;
import sarasvan.billing.main.model.BusinessDetails;
import sarasvan.billing.main.model.InvoiceDetails;

import java.util.List;

@Mapper
public interface InvoiceDetailsMapper {
    InvoiceDetailsMapper INSTANCE = Mappers.getMapper(InvoiceDetailsMapper.class);
    InvoiceDetailsEntity dtoToEntity(InvoiceDetails invoiceDetails);
    InvoiceDetails entityToDto(InvoiceDetailsEntity invoiceDetails);

    List<InvoiceDetailsEntity> dtoListToEntityList(List<InvoiceDetails> dtoList);
    List<InvoiceDetails> entityListToDtoList(List<InvoiceDetailsEntity> entityList);

}
