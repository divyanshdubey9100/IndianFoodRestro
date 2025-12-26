
export const VALIDATION_PATTERNS = {

    email: "^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$",
    password: "(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])[A-Za-z\\d@$!%*?&]{8,}",
    name: "^[a-zA-Z ]*$",
    mobile: "^[0-9]{10}$"

} as const;