package com.driver.services.impl;

import com.driver.services.ConnectionService;
import com.driver.model.*;
import com.driver.repository.ConnectionRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConnectionServiceImpl implements ConnectionService {
    @Autowired
    UserRepository userRepository2;
    @Autowired
    ServiceProviderRepository serviceProviderRepository2;
    @Autowired
    ConnectionRepository connectionRepository2;

    @Override
    public User connect(int userId, String countryName) throws Exception{

        User user = userRepository2.findById(userId).get();
        String newCountryname = countryName.toUpperCase();

        Connection connection = new Connection();
        connection = connectionRepository2.save(connection);

        if(user.isConnected()) throw new Exception("Already connected");

        else if (user.getCountry().equals(newCountryname))
        {
            return user;
        }
        else
        {
            boolean isConnectionPossible = false;
            ServiceProvider availableServiceProvider = null;
            Country availableCountryFromServiceProvider = null;

            List<ServiceProvider> serviceProviderList = user.getServiceProviderList();

            // iterating through serviceproviders

            if(user.getServiceProviderList() == null) throw new Exception("Unable to connect");


            for (ServiceProvider serviceProvider : serviceProviderList) {
                List<Country> countries = serviceProvider.getCountryList();
                // iterating through list of countries provided by seviceproviders
                for (Country country : countries) {
                    // checking is country present or not
                    if(country.getCountryName().equals(newCountryname)){
                        isConnectionPossible = true;
                        if(availableServiceProvider != null){
                            if(availableServiceProvider.getId() > serviceProvider.getId()){
                                availableServiceProvider = serviceProvider;
                            }
                        }else {
                            availableServiceProvider = serviceProvider;
                        }
                        availableCountryFromServiceProvider = country;
                    }
                }
            }



            if(isConnectionPossible == true){
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                user.getConnectionList().add(connection);
                availableServiceProvider.getConnectionList().add(connection);
///////////////////////////////////////////////////////////////////////////////////////////////////////////////
                user.setConnected(true);
//                user.setCountry(availableCountryFromServiceProvider);
                String maskIp = availableCountryFromServiceProvider.getCode() + "."+ availableServiceProvider.getId() +"."+ user.getId();
                user.setMaskedIp(maskIp);

                userRepository2.save(user);

            }else throw new Exception("Unable to connect");

            return user;

        }
    }
    @Override
    public User disconnect(int userId) throws Exception {
        User user = userRepository2.findById(userId).get();
        String maskIp = user.getMaskedIp();
        if(user.isConnected() == false) throw new Exception("Already connected");
        else{
            user.setConnected(false);
            user.setMaskedIp(null);
        }
        user = userRepository2.save(user);
        return user;
    }
    @Override
    public User communicate(int senderId, int receiverId) throws Exception {
//        User recieverUser = userRepository2.findById(receiverId).get();
//        User senderUser = userRepository2.findById(senderId).get();
//
//        // check if receiver is connected to vpn if not then set his current coutry as a original country
//        Country recieverCurrentCountryConnectedToVpn = null;
//        if(recieverUser.isConnected()){
//            String mask = recieverUser.getMaskedIp();
//            String[] splitedMask = mask.split("[.]");
//            String updatedCountryCode = splitedMask[0];
//        }
        User user = userRepository2.findById(senderId).get();
        User user1 = userRepository2.findById(receiverId).get();

        if(user1.getMaskedIp()!=null){
            String str = user1.getMaskedIp();
            String cc = str.substring(0,3); //chopping country code = cc

            if(cc.equals(user.getCountry().getCode()))
                return user;
            else {
                String countryName = "";

                if (cc.equalsIgnoreCase(CountryName.IND.toCode()))
                    countryName = CountryName.IND.toString();
                if (cc.equalsIgnoreCase(CountryName.USA.toCode()))
                    countryName = CountryName.USA.toString();
                if (cc.equalsIgnoreCase(CountryName.JPN.toCode()))
                    countryName = CountryName.JPN.toString();
                if (cc.equalsIgnoreCase(CountryName.CHI.toCode()))
                    countryName = CountryName.CHI.toString();
                if (cc.equalsIgnoreCase(CountryName.AUS.toCode()))
                    countryName = CountryName.AUS.toString();

                User user2 = connect(senderId,countryName);
                if (!user2.isConnected()){
                    throw new Exception("Cannot establish communication");

                }
                else return user2;
            }

        }
        else{
            if(user1.getCountry().equals(user.getCountry())){
                return user;
            }
            String countryName = user1.getCountry().getCountryName().toString();
            User user2 =  connect(senderId,countryName);
            if (!user2.isConnected()){
                throw new Exception("Cannot establish communication");
            }
            else return user2;

        }
    }
}
