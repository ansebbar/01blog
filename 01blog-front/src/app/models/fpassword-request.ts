// import { Fpassword } from "../components/fpassword/fpassword";

export interface FpasswordRequest {
    email: string;
    newpassword?: string;
    pin: number | null;
}