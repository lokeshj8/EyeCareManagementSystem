import React, { useState, useEffect } from 'react';
import { LoginForm } from './components/auth/LoginForm';
import { RegisterForm } from './components/auth/RegisterForm';
import { Sidebar } from './components/layout/Sidebar';
import { DashboardStats } from './components/dashboard/DashboardStats';
import { AppointmentCalendar } from './components/dashboard/AppointmentCalendar';
import { apiService } from './services/api';

function App() {
  const [user, setUser] = useState(null);
  const [currentView, setCurrentView] = useState('dashboard');
  const [showRegister, setShowRegister] = useState(false);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Check if user is already logged in
    const savedUser = apiService.getCurrentUser();
    if (savedUser && apiService.isAuthenticated()) {
      setUser(savedUser);
    }
    setLoading(false);
  }, []);

  const handleLogin = (userData: any) => {
    setUser(userData);
    setCurrentView('dashboard');
  };

  const handleRegister = () => {
    setShowRegister(false);
    // Show success message or redirect to login
  };

  const handleLogout = () => {
    apiService.logout();
    setUser(null);
    setCurrentView('dashboard');
  };

  const renderMainContent = () => {
    switch (currentView) {
      case 'dashboard':
        return (
          <div className="space-y-8">
            <div>
              <h1 className="text-3xl font-bold text-gray-900 mb-2">
                Welcome back, {user.first_name}!
              </h1>
              <p className="text-gray-600">
                Here's what's happening in your eye care practice today.
              </p>
            </div>
            <DashboardStats userRole={user.role} />
            <AppointmentCalendar />
          </div>
        );
      case 'appointments':
        return (
          <div>
            <h1 className="text-3xl font-bold text-gray-900 mb-8">Appointments</h1>
            <AppointmentCalendar />
          </div>
        );
      case 'patients':
        return (
          <div>
            <h1 className="text-3xl font-bold text-gray-900 mb-8">Patients</h1>
            <div className="bg-white rounded-2xl shadow-lg border border-gray-100 p-8 text-center">
              <p className="text-gray-600">Patient management interface coming soon...</p>
            </div>
          </div>
        );
      case 'medical-records':
        return (
          <div>
            <h1 className="text-3xl font-bold text-gray-900 mb-8">Medical Records</h1>
            <div className="bg-white rounded-2xl shadow-lg border border-gray-100 p-8 text-center">
              <p className="text-gray-600">Medical records interface coming soon...</p>
            </div>
          </div>
        );
      case 'doctors':
        return (
          <div>
            <h1 className="text-3xl font-bold text-gray-900 mb-8">Doctors</h1>
            <div className="bg-white rounded-2xl shadow-lg border border-gray-100 p-8 text-center">
              <p className="text-gray-600">Doctor management interface coming soon...</p>
            </div>
          </div>
        );
      case 'profile':
        return (
          <div>
            <h1 className="text-3xl font-bold text-gray-900 mb-8">Profile</h1>
            <div className="bg-white rounded-2xl shadow-lg border border-gray-100 p-8">
              <div className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700">Name</label>
                  <p className="text-gray-900">{user.first_name} {user.last_name}</p>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700">Email</label>
                  <p className="text-gray-900">{user.email}</p>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700">Role</label>
                  <p className="text-gray-900 capitalize">{user.role}</p>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700">Username</label>
                  <p className="text-gray-900">{user.username}</p>
                </div>
              </div>
            </div>
          </div>
        );
      default:
        return (
          <div>
            <h1 className="text-3xl font-bold text-gray-900 mb-8">Dashboard</h1>
            <DashboardStats userRole={user.role} />
            <AppointmentCalendar />
          </div>
        );
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-cyan-50 flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  if (!user) {
    return showRegister ? (
      <RegisterForm 
        onRegister={handleRegister}
        onToggleForm={() => setShowRegister(false)}
      />
    ) : (
      <LoginForm 
        onLogin={handleLogin}
        onToggleForm={() => setShowRegister(true)}
      />
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-cyan-50 flex">
      {/* Sidebar */}
      <div className="w-64 flex-shrink-0">
        <Sidebar
          currentView={currentView}
          onViewChange={setCurrentView}
          userRole={user.role}
          onLogout={handleLogout}
        />
      </div>

      {/* Main Content */}
      <div className="flex-1 overflow-auto">
        <main className="p-8">
          {renderMainContent()}
        </main>
      </div>
    </div>
  );
}

export default App;