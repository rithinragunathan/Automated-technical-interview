import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Users, FileText, CheckCircle, Clock } from 'lucide-react';
import StatCard from '../components/common/StatCard';
import { getAdminStats } from '../api/admin';
import LoadingSpinner from '../components/common/LoadingSpinner';

const AdminDashboardPage = () => {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchStats = async () => {
      try {
        const data = await getAdminStats();
        setStats(data);
      } catch (err) {
        console.error('Admin dashboard error:', err);
        setStats(null);
      } finally {
        setLoading(false);
      }
    };
    fetchStats();
  }, []);

  if (loading) return <LoadingSpinner message="Loading admin dashboard..." />;

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-white mb-2">Admin Dashboard</h1>
        <p className="text-gray-400">System overview and management.</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard title="Total Users" value={stats?.totalUsers} icon={Users} />
        <StatCard title="Total Interviews" value={stats?.totalInterviews} icon={FileText} />
        <StatCard title="Total Problems" value={stats?.totalProblems} icon={CheckCircle} />
        <StatCard title="Total Submissions" value={stats?.totalSubmissions} icon={Clock} />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="bg-[#161b22] border border-[#30363d] rounded-lg p-6">
          <h2 className="text-lg font-semibold text-white mb-4">Quick Links</h2>
          <div className="grid grid-cols-1 gap-4">
            <Link to="/admin/problems" className="p-4 bg-[#0f1117] border border-[#30363d] rounded-lg hover:border-brand-500 transition-colors flex items-center justify-between">
              <div>
                <h3 className="font-medium text-white mb-1">Problem Management</h3>
                <p className="text-sm text-gray-400">Add, edit, or remove coding problems.</p>
              </div>
              <FileText className="w-6 h-6 text-brand-500" />
            </Link>
            <div className="p-4 bg-[#0f1117] border border-[#30363d] rounded-lg opacity-50 cursor-not-allowed flex items-center justify-between">
              <div>
                <h3 className="font-medium text-white mb-1">User Management</h3>
                <p className="text-sm text-gray-400">Manage user roles and accounts.</p>
              </div>
              <Users className="w-6 h-6 text-gray-500" />
            </div>
          </div>
        </div>

        <div className="bg-[#161b22] border border-[#30363d] rounded-lg p-6">
          <h2 className="text-lg font-semibold text-white mb-4">Recent Users</h2>
          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-[#30363d]">
              <thead>
                <tr>
                  <th className="px-3 py-2 text-left text-xs font-medium text-gray-400 uppercase">User</th>
                  <th className="px-3 py-2 text-left text-xs font-medium text-gray-400 uppercase">Role</th>
                  <th className="px-3 py-2 text-left text-xs font-medium text-gray-400 uppercase">Joined</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-[#30363d]">
                {stats?.recentUsers?.map(u => (
                  <tr key={u.id}>
                    <td className="px-3 py-3">
                      <div className="text-sm text-white">{u.name}</div>
                      <div className="text-xs text-gray-500">{u.email}</div>
                    </td>
                    <td className="px-3 py-3">
                      <span className="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-gray-800 text-gray-300 border border-gray-600">
                        {u.role}
                      </span>
                    </td>
                    <td className="px-3 py-3 text-sm text-gray-400">
                      {u.joined}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AdminDashboardPage;
