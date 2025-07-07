export interface Patient {
  id: string;
  name: string;
  age: number;
  phone: string;
  email: string;
  problem: string;
  severity: 'Low' | 'Medium' | 'High' | 'Critical';
  dateAdded: string;
  lastVisit?: string;
  notes?: string;
  status: 'Active' | 'Treated' | 'Follow-up Required';
}

export interface PatientStats {
  total: number;
  active: number;
  treated: number;
  followUp: number;
  critical: number;
}