import React from 'react';
import { 
  Home, 
  Calendar, 
  Users, 
  FileText, 
  User, 
  Settings, 
  LogOut,
  Eye,
  Stethoscope,
  ClipboardList
} from 'lucide-react';

interface SidebarProps {
  currentView: string;
  onViewChange: (view: string) => void;
  userRole: string;
  onLogout: () => void;
}

export const Sidebar: React.FC<SidebarProps> = ({ 
  currentView, 
  onViewChange, 
  userRole, 
  onLogout 
}) => {
  const getMenuItems = () => {
    const baseItems = [
      { id: 'dashboard', label: 'Dashboard', icon: Home },
      { id: 'appointments', label: 'Appointments', icon: Calendar },
      { id: 'medical-records', label: 'Medical Records', icon: FileText },
    ];

    if (userRole === 'doctor' || userRole === 'admin') {
      baseItems.splice(2, 0, { id: 'patients', label: 'Patients', icon: Users });
    }

    if (userRole === 'admin') {
      baseItems.push({ id: 'doctors', label: 'Doctors', icon: Stethoscope });
      baseItems.push({ id: 'reports', label: 'Reports', icon: ClipboardList });
    }

    baseItems.push(
      { id: 'profile', label: 'Profile', icon: User },
      { id: 'settings', label: 'Settings', icon: Settings }
    );

    return baseItems;
  };

  return (
    <div className="bg-white shadow-lg border-r border-gray-200 h-full flex flex-col">
      {/* Logo */}
      <div className="p-6 border-b border-gray-200">
        <div className="flex items-center gap-3">
          <div className="bg-blue-600 p-2 rounded-lg">
            <Eye className="w-6 h-6 text-white" />
          </div>
          <div>
            <h1 className="text-xl font-bold text-gray-900">EyeCare Pro</h1>
            <p className="text-sm text-gray-500 capitalize">{userRole} Portal</p>
          </div>
        </div>
      </div>

      {/* Navigation */}
      <nav className="flex-1 p-4">
        <ul className="space-y-2">
          {getMenuItems().map(item => {
            const Icon = item.icon;
            const isActive = currentView === item.id;
            
            return (
              <li key={item.id}>
                <button
                  onClick={() => onViewChange(item.id)}
                  className={`w-full flex items-center gap-3 px-4 py-3 rounded-lg transition-all ${
                    isActive
                      ? 'bg-blue-50 text-blue-600 border border-blue-200'
                      : 'text-gray-600 hover:bg-gray-50 hover:text-gray-900'
                  }`}
                >
                  <Icon className="w-5 h-5" />
                  <span className="font-medium">{item.label}</span>
                </button>
              </li>
            );
          })}
        </ul>
      </nav>

      {/* Logout */}
      <div className="p-4 border-t border-gray-200">
        <button
          onClick={onLogout}
          className="w-full flex items-center gap-3 px-4 py-3 text-red-600 hover:bg-red-50 rounded-lg transition-all"
        >
          <LogOut className="w-5 h-5" />
          <span className="font-medium">Logout</span>
        </button>
      </div>
    </div>
  );
};