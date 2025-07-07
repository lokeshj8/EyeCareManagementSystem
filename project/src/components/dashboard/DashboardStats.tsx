import React, { useState, useEffect } from 'react';
import { Users, Calendar, FileText, Clock, TrendingUp, AlertTriangle } from 'lucide-react';
import { apiService } from '../../services/api';

interface DashboardStatsProps {
  userRole: string;
}

export const DashboardStats: React.FC<DashboardStatsProps> = ({ userRole }) => {
  const [stats, setStats] = useState({
    totalPatients: 0,
    todayAppointments: 0,
    totalRecords: 0,
    pendingAppointments: 0,
    completedToday: 0,
    criticalCases: 0,
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadStats();
  }, []);

  const loadStats = async () => {
    try {
      setLoading(true);
      const today = new Date().toISOString().split('T')[0];
      
      // Load appointments for today
      const todayAppointments = await apiService.getAppointments({ date: today });
      
      // Load all appointments to get pending count
      const allAppointments = await apiService.getAppointments();
      
      // Load medical records
      const medicalRecords = await apiService.getMedicalRecords();
      
      // Load patients (if user has permission)
      let patients = [];
      if (userRole === 'doctor' || userRole === 'admin') {
        patients = await apiService.getPatients();
      }

      setStats({
        totalPatients: patients.length,
        todayAppointments: todayAppointments.length,
        totalRecords: medicalRecords.length,
        pendingAppointments: allAppointments.filter((apt: any) => apt.status === 'scheduled').length,
        completedToday: todayAppointments.filter((apt: any) => apt.status === 'completed').length,
        criticalCases: medicalRecords.filter((record: any) => 
          record.diagnosis && record.diagnosis.toLowerCase().includes('critical')
        ).length,
      });
    } catch (error) {
      console.error('Failed to load stats:', error);
    } finally {
      setLoading(false);
    }
  };

  const getStatsCards = () => {
    const baseCards = [
      {
        title: "Today's Appointments",
        value: stats.todayAppointments,
        icon: Calendar,
        color: 'bg-blue-500',
        bgColor: 'bg-blue-50',
        change: '+12%',
      },
      {
        title: 'Medical Records',
        value: stats.totalRecords,
        icon: FileText,
        color: 'bg-green-500',
        bgColor: 'bg-green-50',
        change: '+8%',
      },
      {
        title: 'Pending Appointments',
        value: stats.pendingAppointments,
        icon: Clock,
        color: 'bg-yellow-500',
        bgColor: 'bg-yellow-50',
        change: '-3%',
      },
    ];

    if (userRole === 'doctor' || userRole === 'admin') {
      baseCards.unshift({
        title: 'Total Patients',
        value: stats.totalPatients,
        icon: Users,
        color: 'bg-purple-500',
        bgColor: 'bg-purple-50',
        change: '+15%',
      });
    }

    if (userRole === 'doctor') {
      baseCards.push({
        title: 'Completed Today',
        value: stats.completedToday,
        icon: TrendingUp,
        color: 'bg-emerald-500',
        bgColor: 'bg-emerald-50',
        change: '+25%',
      });
    }

    if (userRole === 'admin') {
      baseCards.push({
        title: 'Critical Cases',
        value: stats.criticalCases,
        icon: AlertTriangle,
        color: 'bg-red-500',
        bgColor: 'bg-red-50',
        change: '-5%',
      });
    }

    return baseCards;
  };

  if (loading) {
    return (
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
        {[1, 2, 3, 4].map(i => (
          <div key={i} className="bg-gray-100 rounded-xl p-6 animate-pulse">
            <div className="h-16 bg-gray-200 rounded"></div>
          </div>
        ))}
      </div>
    );
  }

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
      {getStatsCards().map((card, index) => {
        const Icon = card.icon;
        return (
          <div
            key={index}
            className={`${card.bgColor} rounded-xl p-6 border border-gray-100 hover:shadow-lg transition-all duration-300 hover:scale-105`}
          >
            <div className="flex items-center justify-between mb-4">
              <div className={`${card.color} p-3 rounded-lg`}>
                <Icon className="w-6 h-6 text-white" />
              </div>
              <span className={`text-sm font-medium ${
                card.change.startsWith('+') ? 'text-green-600' : 'text-red-600'
              }`}>
                {card.change}
              </span>
            </div>
            <div>
              <p className="text-gray-600 text-sm font-medium mb-1">
                {card.title}
              </p>
              <p className="text-3xl font-bold text-gray-900">
                {card.value}
              </p>
            </div>
          </div>
        );
      })}
    </div>
  );
};