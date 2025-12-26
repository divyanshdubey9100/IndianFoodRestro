
export const VALIDATION_PATTERNS = {

    emailPattern: "^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$",
    passwordPattern: "(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])[A-Za-z\\d@$!%*?&]{8,}",
    namePattern: "^[a-zA-Z ]*$"

}as const;