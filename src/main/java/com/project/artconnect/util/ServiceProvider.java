package com.project.artconnect.util;

import com.project.artconnect.persistence.JdbcArtistDao;
import com.project.artconnect.persistence.JdbcExhibitionDao;
import com.project.artconnect.persistence.JdbcGalleryDao;
import com.project.artconnect.persistence.JdbcMemberDao;
import com.project.artconnect.persistence.JdbcReviewDao;
import com.project.artconnect.persistence.JdbcWorkshopDao;
import com.project.artconnect.service.AddressService;
import com.project.artconnect.service.ArtistService;
import com.project.artconnect.service.ArtistSocialService;
import com.project.artconnect.service.ArtworkService;
import com.project.artconnect.service.BookingService;
import com.project.artconnect.service.CityService;
import com.project.artconnect.service.DisciplineService;
import com.project.artconnect.service.ExhibitionService;
import com.project.artconnect.service.GalleryService;
import com.project.artconnect.service.MemberService;
import com.project.artconnect.service.ReviewService;
import com.project.artconnect.service.WorkshopService;
import com.project.artconnect.service.impl.JdbcAddressService;
import com.project.artconnect.service.impl.JdbcArtistService;
import com.project.artconnect.service.impl.JdbcArtistSocialService;
import com.project.artconnect.service.impl.JdbcArtworkService;
import com.project.artconnect.service.impl.JdbcBookingService;
import com.project.artconnect.service.impl.JdbcCityService;
import com.project.artconnect.service.impl.JdbcDisciplineService;
import com.project.artconnect.service.impl.JdbcExhibitionService;
import com.project.artconnect.service.impl.JdbcGalleryService;
import com.project.artconnect.service.impl.JdbcMemberService;
import com.project.artconnect.service.impl.JdbcReviewService;
import com.project.artconnect.service.impl.JdbcWorkshopService;


public class ServiceProvider {
    // Address
    private static final AddressService addressService = new JdbcAddressService();
    public static AddressService getAddressService() {
        return addressService;
    }

    // Artist
    private static final ArtistService artistService = new JdbcArtistService(new JdbcArtistDao());
    public static ArtistService getArtistService() {
        return artistService;
    }

    // ArtistSocial
    private static final ArtistSocialService artistSocialService = new JdbcArtistSocialService();
    public static ArtistSocialService getArtistSocialService() {
        return artistSocialService;
    }

    // Artwork
    private static final ArtworkService artworkService = new JdbcArtworkService();
    public static ArtworkService getArtworkService() {
        return artworkService;
    }

    // Booking
    private static final BookingService bookingService = new JdbcBookingService();
    public static BookingService getBookingService() {
        return bookingService;
    }

    // City
    private static final CityService cityService = new JdbcCityService();
    public static CityService getCityService() {
        return cityService;
    }

    // Discipline
    private static final DisciplineService disciplineService = new JdbcDisciplineService();
    public static DisciplineService getDisciplineService() {
        return disciplineService;
    }

    // Exhibition
    private static final ExhibitionService exhibitionService = new JdbcExhibitionService(new JdbcExhibitionDao());
    public static ExhibitionService getExhibitionService() {
        return exhibitionService;
    }

    // Gallery
    private static final GalleryService galleryService = new JdbcGalleryService(new JdbcGalleryDao());
    public static GalleryService getGalleryService() {
        return galleryService;
    }

    // Member
    private static final MemberService memberService = new JdbcMemberService(new JdbcMemberDao());
    public static MemberService getMemberService() {
        return memberService;
    }

    // Review
    private static final ReviewService reviewService = new JdbcReviewService(new JdbcReviewDao());
    public static ReviewService getReviewService() {
        return reviewService;
    }
    
    // Workshop
    private static final WorkshopService workshopService = new JdbcWorkshopService(new JdbcWorkshopDao());
    public static WorkshopService getWorkshopService() {
        return workshopService;
    }
}