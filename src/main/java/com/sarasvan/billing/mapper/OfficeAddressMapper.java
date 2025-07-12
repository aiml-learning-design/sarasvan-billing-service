package com.sarasvan.billing.mapper;

import com.sarasvan.billing.entity.OfficeAddressEntity;
import com.sarasvan.billing.model.OfficeAddressDTO;

import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import java.util.List;

@Mapper(componentModel = "spring")
public interface OfficeAddressMapper {

    OfficeAddressMapper INSTANCE = Mappers.getMapper(OfficeAddressMapper.class);

    OfficeAddressEntity dtoToEntity(OfficeAddressDTO dto);

    OfficeAddressDTO entityToDto(OfficeAddressEntity entity);

    List<OfficeAddressEntity> dtoListToEntityList(List<OfficeAddressDTO> dtoList);

    List<OfficeAddressDTO> entityListToDtoList(List<OfficeAddressEntity> entityList);
}