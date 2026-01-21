package com.shopee.shopee_backend.service;

import com.shopee.shopee_backend.dto.CreateFranchiseRequestDto;
import com.shopee.shopee_backend.dto.CreateFranchiseResponseDto;
import com.shopee.shopee_backend.dto.FranchiseDto;
import com.shopee.shopee_backend.dto.UserDto;
import java.util.List;

/**
 * Service Interface defining the business contract for Administrative operations.
 * Acts as an abstraction layer between the Controller (API) and the Implementation.
 */
public interface AdminService {

     /**
      * Fetches a list of all registered franchises.
      * @return List of FranchiseDto containing summary information for the UI.
      */
     List<FranchiseDto> getAllFranchise();

     /**
      * Retrieves all users currently in the system.
      * @return List of UserDto containing non-sensitive user profile data.
      */
     List<UserDto> getAllUsers();

     /**
      * Handles the logic for onboarding a new franchise.
      * This includes verifying the owner's account and linking it to the new outlet.
      * @param request The data required to create a franchise and its admin user.
      * @return CreateFranchiseResponseDto containing the status and temporary credentials.
      */
     CreateFranchiseResponseDto createFranchise(CreateFranchiseRequestDto request);
}