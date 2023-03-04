package com.driver.services.impl;

import com.driver.model.Country;
import com.driver.model.CountryName;
import com.driver.model.ServiceProvider;
import com.driver.model.User;
import com.driver.repository.CountryRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository3;
    @Autowired
    ServiceProviderRepository serviceProviderRepository3;
    @Autowired
    CountryRepository countryRepository3;

    @Override
    public User register(String username, String password, String countryName) throws Exception{
        // creating new objects of User and Country
        User user = new User();
        Country country = new Country();

        // setting all attributes of user
        user.setUsername(username);
        user.setPassword(password);


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        // setting all attributes of country
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
        }

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        // setting foreign keys
        user.setOriginalCountry(country);
        country.setUser(user);

        // saving all entities

        user = userRepository3.save(user);
        countryRepository3.save(country);

        // adding ips
        String originalIp = enumCountryName.toCode() + "." + user.getId();
        user.setConnected(false);
        user.setMaskedIp(null);

        userRepository3.save(user);

        return user;

    }

    @Override
    public User subscribe(Integer userId, Integer serviceProviderId) {
        User user = userRepository3.findById(userId).get();
        ServiceProvider serviceProvider = serviceProviderRepository3.findById(serviceProviderId).get();

        user.getServiceProviderList().add(serviceProvider);
        serviceProvider.getUsers().add(user);

        return user;
    }
}
