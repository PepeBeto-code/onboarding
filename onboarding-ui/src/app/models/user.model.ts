export interface RegisterRequest {
    username: string;
    email: string;
    password: string;
  }
  
  export interface LoginRequest {
    email: string;
    password: string;
  }
  
  export interface AuthResponse {
    token: string;
    initOnboarding: boolean;
    completeOnboarding: boolean;
    user: User;
  }

  export interface User {
    id: number;
    username: string;
  }

  export interface Personal {
    email: string;
    phone: string;
    birthday: string;
  }

  export interface Onboardin {
    stepCompleted: number;
    isComplete: boolean;
  }


  