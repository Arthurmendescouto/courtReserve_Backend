package com.example.court_reserve.mapper;

import com.example.court_reserve.controller.request.BookingRequest;
import com.example.court_reserve.controller.response.BookingResponse;
import com.example.court_reserve.entity.Booking;
import com.example.court_reserve.entity.Court;
import com.example.court_reserve.entity.User;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BookingMapper {
    public static Booking toBooking(BookingRequest bookingRequest, Court court, User user){
        return Booking.create(
                user,
                court,
                bookingRequest.startDateTime(),
                bookingRequest.endDateTime()
        );
    }
    public static BookingResponse toBookingResponse(Booking booking){
        User user = booking.getUser();
        Court court = booking.getCourt();

        BookingResponse.UserInfo userInfo = new BookingResponse.UserInfo(
                user.getId(),
                user.getName(),
                user.getEmail()
                ,user.getRole()
        );

        BookingResponse.CourtInfo courtInfo = new BookingResponse.CourtInfo(
                court.getId(),
                court.getName(),
                court.getSportType(),
                court.getPricePerHour()
        );

        return new BookingResponse(
                booking.getId(),
                booking.getStartDateTime(),
                booking.getEndDateTime(),
                booking.getTotalPrice(),
                userInfo,
                courtInfo
        );
    }
}
