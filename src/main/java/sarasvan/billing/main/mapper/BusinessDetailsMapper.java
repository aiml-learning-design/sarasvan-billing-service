package sarasvan.billing.main.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import sarasvan.billing.main.entity.BusinessDetailsEntity;
import sarasvan.billing.main.model.BusinessDetails;

import java.util.List;

@Mapper
public interface BusinessDetailsMapper {
    BusinessDetailsMapper INSTANCE = Mappers.getMapper(BusinessDetailsMapper.class);
    BusinessDetailsEntity dtoToEntity(BusinessDetails businessDetails);
    BusinessDetails entityToDto(BusinessDetailsEntity businessDetails);

    List<BusinessDetailsEntity> dtoListToEntityList(List<BusinessDetails> dtoList);
    List<BusinessDetails> entityListToDtoList(List<BusinessDetailsEntity> entityList);
}
