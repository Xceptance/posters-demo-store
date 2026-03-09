package com.xceptance.posters.service;

import com.xceptance.posters.entity.Site;
import com.xceptance.posters.entity.SiteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class SiteConfigurationService {

    private final SiteRepository siteRepository;

    public SiteConfigurationService(SiteRepository siteRepository) {
        this.siteRepository = siteRepository;
    }

    public Optional<Site> getSiteByName(String name) {
        return siteRepository.findByName(name);
    }

    public List<Site> getAllSites() {
        return siteRepository.findAll();
    }
}
