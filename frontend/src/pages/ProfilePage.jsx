import React, { useState, useEffect } from 'react';
import { User, Mail, BookOpen, Building, GraduationCap, Code, Save, Loader2 } from 'lucide-react';
import useAuth from '../hooks/useAuth';
import { updateProfile } from '../api/users';
import toast from 'react-hot-toast';
import LoadingSpinner from '../components/common/LoadingSpinner';

const ProfilePage = () => {
  const { user, loading: authLoading } = useAuth();
  const [formData, setFormData] = useState(null);
  const [isEditing, setIsEditing] = useState(false);
  const [isSaving, setIsSaving] = useState(false);

  useEffect(() => {
    if (user) {
      setFormData({
        name: user.name || '',
        email: user.email || '',
        college: user.college || '',
        department: user.department || '',
        yearOfStudy: user.yearOfStudy || '',
        skills: user.skills ? user.skills.join(', ') : '',
        preferredLanguage: user.preferredLanguage || 'JAVA'
      });
    }
  }, [user]);

  if (authLoading || !formData) return <LoadingSpinner message="Loading profile..." />;

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsSaving(true);
    try {
      const payload = {
        ...formData,
        skills: formData.skills.split(',').map(s => s.trim()).filter(s => s)
      };
      await updateProfile(payload);
      toast.success('Profile updated successfully');
      setIsEditing(false);
    } catch (error) {
      toast.error('Failed to update profile');
    } finally {
      setIsSaving(false);
    }
  };

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-white">Your Profile</h1>
        {!isEditing && (
          <button
            onClick={() => setIsEditing(true)}
            className="px-4 py-2 bg-[#30363d] hover:bg-[#484f58] text-white rounded-md transition-colors text-sm font-medium"
          >
            Edit Profile
          </button>
        )}
      </div>

      <div className="bg-[#161b22] border border-[#30363d] rounded-lg overflow-hidden">
        <div className="p-6 md:p-8">
          <div className="flex flex-col md:flex-row items-center md:items-start space-y-4 md:space-y-0 md:space-x-8 mb-8">
            <div className="w-24 h-24 rounded-full bg-brand-600 flex items-center justify-center text-4xl text-white font-bold border-4 border-[#0f1117]">
              {user?.name?.charAt(0)?.toUpperCase() || 'U'}
            </div>
            <div className="text-center md:text-left">
              <h2 className="text-2xl font-bold text-white mb-1">{user?.name}</h2>
              <p className="text-gray-400">{user?.email}</p>
              <div className="mt-2 inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-brand-900/30 text-brand-400 border border-brand-900/50">
                {user?.role || 'USER'}
              </div>
            </div>
          </div>

          <form onSubmit={handleSubmit} className="space-y-6">
            <div className="grid grid-cols-1 gap-y-6 gap-x-4 sm:grid-cols-2">
              <div>
                <label className="block text-sm font-medium text-gray-400 mb-1">Full Name</label>
                <div className="relative rounded-md shadow-sm">
                  <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                    <User className="h-4 w-4 text-gray-500" />
                  </div>
                  <input
                    name="name" type="text" disabled={!isEditing}
                    value={formData.name} onChange={handleChange}
                    className="block w-full pl-10 pr-3 py-2 bg-[#0f1117] border border-[#30363d] rounded-md text-white disabled:opacity-70 disabled:bg-[#0f1117]/50"
                  />
                </div>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-400 mb-1">Email</label>
                <div className="relative rounded-md shadow-sm">
                  <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                    <Mail className="h-4 w-4 text-gray-500" />
                  </div>
                  <input
                    name="email" type="email" disabled
                    value={formData.email}
                    className="block w-full pl-10 pr-3 py-2 bg-[#0f1117]/50 border border-[#30363d] rounded-md text-gray-500 opacity-70"
                  />
                </div>
                <p className="mt-1 text-xs text-gray-500">Email cannot be changed</p>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-400 mb-1">College</label>
                <div className="relative rounded-md shadow-sm">
                  <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                    <Building className="h-4 w-4 text-gray-500" />
                  </div>
                  <input
                    name="college" type="text" disabled={!isEditing}
                    value={formData.college} onChange={handleChange}
                    className="block w-full pl-10 pr-3 py-2 bg-[#0f1117] border border-[#30363d] rounded-md text-white disabled:opacity-70 disabled:bg-[#0f1117]/50"
                  />
                </div>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-400 mb-1">Department</label>
                <div className="relative rounded-md shadow-sm">
                  <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                    <BookOpen className="h-4 w-4 text-gray-500" />
                  </div>
                  <input
                    name="department" type="text" disabled={!isEditing}
                    value={formData.department} onChange={handleChange}
                    className="block w-full pl-10 pr-3 py-2 bg-[#0f1117] border border-[#30363d] rounded-md text-white disabled:opacity-70 disabled:bg-[#0f1117]/50"
                  />
                </div>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-400 mb-1">Year of Study</label>
                <div className="relative rounded-md shadow-sm">
                  <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                    <GraduationCap className="h-4 w-4 text-gray-500" />
                  </div>
                  <select
                    name="yearOfStudy" disabled={!isEditing}
                    value={formData.yearOfStudy} onChange={handleChange}
                    className="block w-full pl-10 pr-3 py-2 bg-[#0f1117] border border-[#30363d] rounded-md text-white disabled:opacity-70 disabled:bg-[#0f1117]/50"
                  >
                    <option value="">Select Year</option>
                    <option value="1">1st Year</option>
                    <option value="2">2nd Year</option>
                    <option value="3">3rd Year</option>
                    <option value="4">4th Year</option>
                    <option value="Graduated">Graduated</option>
                  </select>
                </div>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-400 mb-1">Preferred Language</label>
                <div className="relative rounded-md shadow-sm">
                  <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                    <Code className="h-4 w-4 text-gray-500" />
                  </div>
                  <select
                    name="preferredLanguage" disabled={!isEditing}
                    value={formData.preferredLanguage} onChange={handleChange}
                    className="block w-full pl-10 pr-3 py-2 bg-[#0f1117] border border-[#30363d] rounded-md text-white disabled:opacity-70 disabled:bg-[#0f1117]/50"
                  >
                    <option value="JAVA">Java</option>
                    <option value="PYTHON">Python</option>
                    <option value="CPP">C++</option>
                    <option value="JAVASCRIPT">JavaScript</option>
                  </select>
                </div>
              </div>

              <div className="sm:col-span-2">
                <label className="block text-sm font-medium text-gray-400 mb-1">Skills</label>
                <input
                  name="skills" type="text" disabled={!isEditing}
                  value={formData.skills} onChange={handleChange}
                  className="block w-full px-3 py-2 bg-[#0f1117] border border-[#30363d] rounded-md text-white disabled:opacity-70 disabled:bg-[#0f1117]/50"
                  placeholder="e.g. React, Spring Boot, MySQL"
                />
              </div>
            </div>

            {isEditing && (
              <div className="flex justify-end space-x-3 pt-6 border-t border-[#30363d]">
                <button
                  type="button"
                  onClick={() => {
                    setIsEditing(false);
                    // Reset to user data
                    setFormData({
                      name: user.name || '',
                      email: user.email || '',
                      college: user.college || '',
                      department: user.department || '',
                      yearOfStudy: user.yearOfStudy || '',
                      skills: user.skills ? user.skills.join(', ') : '',
                      preferredLanguage: user.preferredLanguage || 'JAVA'
                    });
                  }}
                  className="px-4 py-2 text-sm font-medium text-gray-300 bg-transparent border border-[#30363d] rounded-md hover:bg-[#30363d]"
                  disabled={isSaving}
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={isSaving}
                  className="flex items-center px-4 py-2 text-sm font-medium text-white bg-brand-600 rounded-md hover:bg-brand-700 disabled:opacity-50"
                >
                  {isSaving ? <Loader2 className="w-4 h-4 mr-2 animate-spin" /> : <Save className="w-4 h-4 mr-2" />}
                  Save Changes
                </button>
              </div>
            )}
          </form>
        </div>
      </div>
    </div>
  );
};

export default ProfilePage;
