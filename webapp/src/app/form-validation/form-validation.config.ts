
export const VALIDATION_PATTERNS = {

    email: "^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$",
    password: "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
    name: "^[a-zA-Z ]*$",
    mobile: "^[0-9]{10}$",
    dateofbirth: "^(19|20)\d{2}-(0[1-9]|1[0-2])-(0[1-9]|[12]\d|3[01])$"

} as const;