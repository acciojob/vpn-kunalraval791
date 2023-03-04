package com.driver.services.impl;

import com.driver.model.Admin;
import com.driver.model.Country;
import com.driver.model.CountryName;
import com.driver.model.ServiceProvider;
import com.driver.repository.AdminRepository;
import com.driver.repository.CountryRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    AdminRepository adminRepository1;

    @Autowired
    ServiceProviderRepository serviceProviderRepository1;

    @Autowired
    CountryRepository countryRepository1;

    @Override
    public Admin register(String username, String password) {
        Admin admin = new Admin();
        // setting all attributes
        admin.setUsername(username);
        admin.setPassword(password);

        adminRepository1.save(admin);

        return admin;
    }

    @Override
    public Admin addServiceProvider(int adminId, String providerName) {
        Admin admin = adminRepository1.findById(adminId).get();

        ServiceProvider serviceProvider = new ServiceProvider();
        serviceProvider.setName(providerName);

        serviceProviderRepository1.save(serviceProvider);
        admin.getServiceProviders().add(serviceProvider);

        adminRepository1.save(admin);

        return admin;
    }

    @Override
    public ServiceProvider addCountry(int serviceProviderId, String countryName) throws Exception{

        // Important api
        ServiceProvider serviceProvider = serviceProviderRepository1.findById(serviceProviderId).get();

        Country country = new Country();
        String newCountry = countryName.toUpperCase();
        CountryName enumCountryName = null;
        boolean isCountryValid = false;

        // looping through enums
        for (CountryName name : CountryName.values()) {
            if(newCountry.equals(name)){
                isCountryValid = true;
                enumCountryName = name;
                break;
            }
        }

        // throwing exception if country is not present
        if(isCountryValid == false) throw new Exception("Country not found");

        if(enumCountryName != null){
            country.setCountryName(enumCountryName);
            country.setCode(enumCountryName.toCode());
            country.setUser(null);
        }

        // saving all entities and updating foreign keys
        countryRepository1.save(country);

        serviceProvider.getCountryList().add(country);
        serviceProviderRepository1.save(serviceProvider);

        return serviceProvider;
    }
}
