package com.sarasvan.billing.mapper;

import com.sarasvan.billing.entity.BusinessDetailsEntity;
import com.sarasvan.billing.model.BusinessDetails;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface BusinessDetailsMapper {
    BusinessDetailsMapper INSTANCE = Mappers.getMapper(BusinessDetailsMapper.class);
    BusinessDetailsEntity dtoToEntity(BusinessDetails businessDetails);
    BusinessDetails entityToDto(BusinessDetailsEntity businessDetails);

    List<BusinessDetailsEntity> dtoListToEntityList(List<BusinessDetails> dtoList);
    List<BusinessDetails> entityListToDtoList(List<BusinessDetailsEntity> entityList);
}
