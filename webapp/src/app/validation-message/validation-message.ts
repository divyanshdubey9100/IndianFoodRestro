export const VALIDATION_ERRORS ={

            // Standard Validators
            required: 'This field is required.',
            email: 'Please enter a valid email address.',
            pattern: 'The format is invalid.',
            
            // Custom field-specific validators
            name:'Please enter a valid name with only letters and spaces.',
            mobile:'Please enter a valid 10-digit mobile number.',
            passwordStrength: 'Password must contain at least 8 characters including one uppercase letter, one lowercase letter, one number, and one special character (@$!%*?&).',
            dateofbirth: 'Please enter date in YYYY-MM-DD format.',

            // Dynamic Validators (using value)
            minlength: (validatorValue: any) => `Minimum length is ${validatorValue.requiredLength} characters.`,
            maxlength: (validatorValue: any) => `Maximum length is ${validatorValue.requiredLength} characters.`,

            // Date validation errors
            invalidDate: 'Please enter a valid date.',
            futureDate: 'Date of birth cannot be in the future.',
            yearOutOfRange: 'Please enter a valid birth year.',
            underAge: 'You must be at least 18 years old to register.',
            
            // Other custom validators
            cannotContainSpace: 'Spaces are not allowed in this field.',
            
            // Form-level errors
            passwordMismatch: 'Passwords do not match.'

}as const;