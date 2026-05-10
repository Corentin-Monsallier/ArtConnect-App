package com.project.artconnect.util;

import com.project.artconnect.persistence.JdbcArtistDao;
import com.project.artconnect.service.*;
import com.project.artconnect.service.impl.*;
import com.project.artconnect.service.impl.JdbcArtistService;

/**
 * Service Provider to manage singleton instances of services and handle their
 * initialization.
 */
public class ServiceProvider {

    private static final ArtistService artistService = new JdbcArtistService(new JdbcArtistDao());

    public static ArtistService getArtistService() {
        return artistService;
    }

    /*
     * public static ArtworkService getArtworkService() {
     * return artworkService;
     * }
     * 
     * public static GalleryService getGalleryService() {
     * return galleryService;
     * }
     * 
     * public static WorkshopService getWorkshopService() {
     * return workshopService;
     * }
     * 
     * public static CommunityService getCommunityService() {
     * return communityService;
     * }
     */
}
