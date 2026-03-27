package com.argenischacon.dentalclinic.mappers;

import com.argenischacon.dentalclinic.dto.appointment.AppointmentListDto;
import com.argenischacon.dentalclinic.dto.appointment.AppointmentNestedDto;
import com.argenischacon.dentalclinic.dto.appointment.AppointmentRequestDto;
import com.argenischacon.dentalclinic.dto.appointment.AppointmentResponseDto;
import com.argenischacon.dentalclinic.model.Appointment;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", uses = {
        PatientMapper.class,
        DentistMapper.class,
        ServiceMapper.class,
        ReceptionistMapper.class
})
public interface AppointmentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "appointmentCode", ignore = true)
    @Mapping(target = "endTime", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "dentist", ignore = true)
    @Mapping(target = "patient", ignore = true)
    @Mapping(target = "service", ignore = true)
    Appointment toEntity(AppointmentRequestDto dto);

    AppointmentResponseDto toResponseDto(Appointment entity);

    @Mapping(target = "patientFullName", expression = "java(entity.getPatient().getName() + \" \" + entity.getPatient().getLastName())")
    @Mapping(target = "dentistFullName", expression = "java(entity.getDentist().getName() + \" \" + entity.getDentist().getLastName())")
    @Mapping(target = "serviceName", source = "service.name")
    AppointmentListDto toListDto(Appointment entity);

    AppointmentNestedDto toNestedDto(Appointment entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "appointmentCode", ignore = true)
    @Mapping(target = "endTime", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "dentist", ignore = true)
    @Mapping(target = "patient", ignore = true)
    @Mapping(target = "service", ignore = true)
    Appointment updateEntityFromDto(AppointmentRequestDto dto, @MappingTarget Appointment entity);
}
