import { Patient, PatientStats } from '../types/Patient';

class PatientService {
  private storageKey = 'eyecare_patients';

  getAllPatients(): Patient[] {
    const stored = localStorage.getItem(this.storageKey);
    return stored ? JSON.parse(stored) : [];
  }

  addPatient(patient: Omit<Patient, 'id' | 'dateAdded'>): Patient {
    const patients = this.getAllPatients();
    const newPatient: Patient = {
      ...patient,
      id: this.generateId(),
      dateAdded: new Date().toISOString(),
    };
    
    patients.push(newPatient);
    this.savePatients(patients);
    return newPatient;
  }

  updatePatient(id: string, updates: Partial<Patient>): Patient | null {
    const patients = this.getAllPatients();
    const index = patients.findIndex(p => p.id === id);
    
    if (index === -1) return null;
    
    patients[index] = { ...patients[index], ...updates };
    this.savePatients(patients);
    return patients[index];
  }

  deletePatient(id: string): boolean {
    const patients = this.getAllPatients();
    const filtered = patients.filter(p => p.id !== id);
    
    if (filtered.length === patients.length) return false;
    
    this.savePatients(filtered);
    return true;
  }

  getPatientById(id: string): Patient | null {
    const patients = this.getAllPatients();
    return patients.find(p => p.id === id) || null;
  }

  searchPatients(query: string): Patient[] {
    const patients = this.getAllPatients();
    const lowercaseQuery = query.toLowerCase();
    
    return patients.filter(patient =>
      patient.name.toLowerCase().includes(lowercaseQuery) ||
      patient.problem.toLowerCase().includes(lowercaseQuery) ||
      patient.email.toLowerCase().includes(lowercaseQuery)
    );
  }

  getPatientStats(): PatientStats {
    const patients = this.getAllPatients();
    
    return {
      total: patients.length,
      active: patients.filter(p => p.status === 'Active').length,
      treated: patients.filter(p => p.status === 'Treated').length,
      followUp: patients.filter(p => p.status === 'Follow-up Required').length,
      critical: patients.filter(p => p.severity === 'Critical').length,
    };
  }

  private savePatients(patients: Patient[]): void {
    localStorage.setItem(this.storageKey, JSON.stringify(patients));
  }

  private generateId(): string {
    return Date.now().toString(36) + Math.random().toString(36).substr(2);
  }
}

export const patientService = new PatientService();