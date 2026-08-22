package com.spring.beatmarket.infrastructure.domain.catalog.controller.genre;

import com.spring.beatmarket.domain.catalog.dto.GenreDto;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Slice;

import java.util.List;

@Mapper(componentModel = "spring")
interface GenreControllerMapper {

    GenreDto.Create toCreateDto(GenreApiDto.Request request);

    GenreDto.Update toUpdateDto(GenreApiDto.Request request);

    GenreApiDto.InfoResponse toInfoResponse(GenreDto.Info dto);

    GenreApiDto.DetailsResponse toDetailsResponse(GenreDto.Details dto);

    GenreApiDto.SummaryResponse toSummaryResponse(GenreDto.Summary dto);

    GenreApiDto.TransferResponse toResponse (GenreDto.Transfer dto);

    List<GenreApiDto.SummaryResponse> toSummaryResponseList(List<GenreDto.Summary> list);

    default GenreApiDto.GetAllResponse toGetAllResponse(Slice<GenreDto.Summary> slice) {
        return new GenreApiDto.GetAllResponse(this.toSummaryResponseList(slice.getContent()), slice.hasNext());
    }
}