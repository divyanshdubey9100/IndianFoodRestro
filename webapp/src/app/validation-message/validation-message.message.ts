export const VALIDATION_ERRORS ={

            // Standard Validators
            required: 'This field is required.',
            email: 'Please enter a valid email address.',
            pattern: 'The format is invalid.',
            name:'entered name should be valid String',

            // Dynamic Validators (using value)
            minlength: (validatorValue: any) => `Minimum length is ${validatorValue.requiredLength} characters.`,
            maxlength: (validatorValue: any) => `Maximum length is ${validatorValue.requiredLength} characters.`,

            // Custom Validators (match names from your ValidationService)
            cannotContainSpace: 'Spaces are not allowed in this field.',
            passwordStrength: ''
       

}as const;