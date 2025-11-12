package com.pma.validators;

import com.pma.dao.IEmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class UniqueValidator implements ConstraintValidator<UniqueValue, String>{

    @Autowired
    IEmployeeRepository empRepo;

    public UniqueValidator() {
        // Required no-arg constructor
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context){
        System.out.println("Injected empRepo: " + empRepo);
        System.out.println("Entered validation method...");
        return empRepo.findByEmail(value) == null;

    }

//    public boolean isValid(String value, ConstraintValidatorContext context){return true;}

}
