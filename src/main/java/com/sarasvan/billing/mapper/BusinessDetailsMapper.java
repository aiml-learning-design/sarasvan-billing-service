package com.sarasvan.billing.mapper;

import com.sarasvan.billing.entity.BusinessDetailsEntity;
import com.sarasvan.billing.model.BusinessDetailsDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface BusinessDetailsMapper {
    BusinessDetailsMapper INSTANCE = Mappers.getMapper(BusinessDetailsMapper.class);
    BusinessDetailsEntity dtoToEntity(BusinessDetailsDTO businessDetailsDTO);
    BusinessDetailsDTO entityToDto(BusinessDetailsEntity businessDetails);

    List<BusinessDetailsEntity> dtoListToEntityList(List<BusinessDetailsDTO> dtoList);
    List<BusinessDetailsDTO> entityListToDtoList(List<BusinessDetailsEntity> entityList);
}
