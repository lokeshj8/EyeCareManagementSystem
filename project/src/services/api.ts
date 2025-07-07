const API_BASE_URL = 'http://localhost:8080/api';

class ApiService {
  private token: string | null = null;

  constructor() {
    this.token = localStorage.getItem('authToken');
  }

  private async request(endpoint: string, options: RequestInit = {}) {
    const url = `${API_BASE_URL}${endpoint}`;
    const config: RequestInit = {
      headers: {
        'Content-Type': 'application/json',
        ...(this.token && { Authorization: `Bearer ${this.token}` }),
        ...options.headers,
      },
      ...options,
    };

    try {
      const response = await fetch(url, config);
      const data = await response.json();

      if (!response.ok) {
        throw new Error(data.error || 'API request failed');
      }

      return data;
    } catch (error) {
      console.error('API request error:', error);
      throw error;
    }
  }

  // Auth methods
  async login(username: string, password: string) {
    const data = await this.request('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ username, password }),
    });
    
    if (data.token) {
      this.token = data.token;
      localStorage.setItem('authToken', data.token);
      localStorage.setItem('user', JSON.stringify(data.user));
    }
    
    return data;
  }

  async register(userData: any) {
    return this.request('/auth/register', {
      method: 'POST',
      body: JSON.stringify(userData),
    });
  }

  logout() {
    this.token = null;
    localStorage.removeItem('authToken');
    localStorage.removeItem('user');
  }

  // Appointments
  async getAppointments(filters: any = {}) {
    const params = new URLSearchParams(filters);
    return this.request(`/appointments?${params}`);
  }

  async createAppointment(appointmentData: any) {
    return this.request('/appointments', {
      method: 'POST',
      body: JSON.stringify(appointmentData),
    });
  }

  async updateAppointment(id: string, updates: any) {
    return this.request(`/appointments/${id}`, {
      method: 'PUT',
      body: JSON.stringify(updates),
    });
  }

  async deleteAppointment(id: string) {
    return this.request(`/appointments/${id}`, {
      method: 'DELETE',
    });
  }

  // Medical Records
  async getMedicalRecords(filters: any = {}) {
    const params = new URLSearchParams(filters);
    return this.request(`/medical-records?${params}`);
  }

  async createMedicalRecord(recordData: any) {
    return this.request('/medical-records', {
      method: 'POST',
      body: JSON.stringify(recordData),
    });
  }

  async updateMedicalRecord(id: string, updates: any) {
    return this.request(`/medical-records/${id}`, {
      method: 'PUT',
      body: JSON.stringify(updates),
    });
  }

  // Patients
  async getPatients(filters: any = {}) {
    const params = new URLSearchParams(filters);
    return this.request(`/patients?${params}`);
  }

  async getPatient(id: string) {
    return this.request(`/patients/${id}`);
  }

  async updatePatient(id: string, updates: any) {
    return this.request(`/patients/${id}`, {
      method: 'PUT',
      body: JSON.stringify(updates),
    });
  }

  async getPatientAppointments(id: string, filters: any = {}) {
    const params = new URLSearchParams(filters);
    return this.request(`/patients/${id}/appointments?${params}`);
  }

  async getPatientMedicalRecords(id: string) {
    return this.request(`/patients/${id}/medical-records`);
  }

  // Doctors
  async getDoctors(filters: any = {}) {
    const params = new URLSearchParams(filters);
    return this.request(`/doctors?${params}`);
  }

  async getDoctor(id: string) {
    return this.request(`/doctors/${id}`);
  }

  async updateDoctor(id: string, updates: any) {
    return this.request(`/doctors/${id}`, {
      method: 'PUT',
      body: JSON.stringify(updates),
    });
  }

  async getDoctorAppointments(id: string, filters: any = {}) {
    const params = new URLSearchParams(filters);
    return this.request(`/doctors/${id}/appointments?${params}`);
  }

  // Utility methods
  getCurrentUser() {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
  }

  isAuthenticated() {
    return !!this.token;
  }
}

export const apiService = new ApiService();