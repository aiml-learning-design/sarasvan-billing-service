package com.sarasvan.billing.mapper;

import com.sarasvan.billing.entity.InvoiceDetailsEntity;
import com.sarasvan.billing.entity.UserEntity;
import com.sarasvan.billing.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface UsersMapper {
    UsersMapper INSTANCE = Mappers.getMapper(UsersMapper.class);
    UserEntity dtoToEntity(User user);
    User entityToDto(UserEntity userEntity);

    List<InvoiceDetailsEntity> dtoListToEntityList(List<User> dtoList);
    List<User> entityListToDtoList(List<UserEntity> userEntities);

}

