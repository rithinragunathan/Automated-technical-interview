import React, { useState } from 'react';
import { Link, useNavigate, Navigate } from 'react-router-dom';
import { User, Mail, Lock, BookOpen, Building, GraduationCap, Code, Loader2 } from 'lucide-react';
import useAuth from '../hooks/useAuth';
import { validateEmail, validatePassword } from '../utils/validation';

const RegisterPage = () => {
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    password: '',
    confirmPassword: '',
    college: '',
    department: '',
    yearOfStudy: '',
    skills: '',
    preferredLanguage: 'JAVA'
  });
  const [errors, setErrors] = useState({});
  const [isSubmitting, setIsSubmitting] = useState(false);
  const { register, isAuthenticated } = useAuth();
  const navigate = useNavigate();

  if (isAuthenticated) {
    return <Navigate to="/dashboard" replace />;
  }

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    if (errors[name]) {
      setErrors(prev => ({ ...prev, [name]: null }));
    }
  };

  const validate = () => {
    const newErrors = {};
    if (!formData.name.trim()) newErrors.name = 'Full name is required';
    if (!validateEmail(formData.email)) newErrors.email = 'Valid email is required';
    if (!validatePassword(formData.password)) newErrors.password = 'Password must be at least 6 characters';
    if (formData.password !== formData.confirmPassword) newErrors.confirmPassword = 'Passwords do not match';
    
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validate()) return;

    setIsSubmitting(true);
    
    // Format data for API
    const apiData = {
      ...formData,
      skills: formData.skills.split(',').map(s => s.trim()).filter(s => s)
    };
    
    const result = await register(apiData);
    setIsSubmitting(false);

    if (result.success) {
      navigate('/dashboard');
    }
  };

  return (
    <div className="w-full">
      <form onSubmit={handleSubmit} className="space-y-6">
        <div className="grid grid-cols-1 gap-y-6 gap-x-4 sm:grid-cols-2">
          
          <div className="sm:col-span-2">
            <label className="block text-sm font-medium text-gray-300">Full Name</label>
            <div className="mt-1 relative rounded-md shadow-sm">
              <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <User className="h-5 w-5 text-gray-500" />
              </div>
              <input
                name="name" type="text" required value={formData.name} onChange={handleChange}
                className={`block w-full pl-10 pr-3 py-2 bg-[#0f1117] border ${errors.name ? 'border-red-500' : 'border-[#30363d]'} rounded-md text-white focus:ring-brand-500 focus:border-brand-500`}
                placeholder="John Doe"
              />
            </div>
            {errors.name && <p className="mt-1 text-sm text-red-500">{errors.name}</p>}
          </div>

          <div className="sm:col-span-2">
            <label className="block text-sm font-medium text-gray-300">Email address</label>
            <div className="mt-1 relative rounded-md shadow-sm">
              <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <Mail className="h-5 w-5 text-gray-500" />
              </div>
              <input
                name="email" type="email" required value={formData.email} onChange={handleChange}
                className={`block w-full pl-10 pr-3 py-2 bg-[#0f1117] border ${errors.email ? 'border-red-500' : 'border-[#30363d]'} rounded-md text-white focus:ring-brand-500 focus:border-brand-500`}
                placeholder="you@example.com"
              />
            </div>
            {errors.email && <p className="mt-1 text-sm text-red-500">{errors.email}</p>}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-300">Password</label>
            <div className="mt-1 relative rounded-md shadow-sm">
              <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <Lock className="h-5 w-5 text-gray-500" />
              </div>
              <input
                name="password" type="password" required value={formData.password} onChange={handleChange}
                className={`block w-full pl-10 pr-3 py-2 bg-[#0f1117] border ${errors.password ? 'border-red-500' : 'border-[#30363d]'} rounded-md text-white focus:ring-brand-500 focus:border-brand-500`}
                placeholder="••••••••"
              />
            </div>
            {errors.password && <p className="mt-1 text-sm text-red-500">{errors.password}</p>}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-300">Confirm Password</label>
            <div className="mt-1 relative rounded-md shadow-sm">
              <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <Lock className="h-5 w-5 text-gray-500" />
              </div>
              <input
                name="confirmPassword" type="password" required value={formData.confirmPassword} onChange={handleChange}
                className={`block w-full pl-10 pr-3 py-2 bg-[#0f1117] border ${errors.confirmPassword ? 'border-red-500' : 'border-[#30363d]'} rounded-md text-white focus:ring-brand-500 focus:border-brand-500`}
                placeholder="••••••••"
              />
            </div>
            {errors.confirmPassword && <p className="mt-1 text-sm text-red-500">{errors.confirmPassword}</p>}
          </div>

          <div className="sm:col-span-2 pt-4 pb-2 border-b border-[#30363d]">
            <h4 className="text-sm font-medium text-gray-400">Academic & Technical Profile (Optional)</h4>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-300">College / University</label>
            <div className="mt-1 relative rounded-md shadow-sm">
              <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <Building className="h-5 w-5 text-gray-500" />
              </div>
              <input
                name="college" type="text" value={formData.college} onChange={handleChange}
                className="block w-full pl-10 pr-3 py-2 bg-[#0f1117] border border-[#30363d] rounded-md text-white focus:ring-brand-500 focus:border-brand-500"
              />
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-300">Department</label>
            <div className="mt-1 relative rounded-md shadow-sm">
              <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <BookOpen className="h-5 w-5 text-gray-500" />
              </div>
              <input
                name="department" type="text" value={formData.department} onChange={handleChange}
                className="block w-full pl-10 pr-3 py-2 bg-[#0f1117] border border-[#30363d] rounded-md text-white focus:ring-brand-500 focus:border-brand-500"
              />
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-300">Year of Study</label>
            <div className="mt-1 relative rounded-md shadow-sm">
              <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <GraduationCap className="h-5 w-5 text-gray-500" />
              </div>
              <select
                name="yearOfStudy" value={formData.yearOfStudy} onChange={handleChange}
                className="block w-full pl-10 pr-3 py-2 bg-[#0f1117] border border-[#30363d] rounded-md text-white focus:ring-brand-500 focus:border-brand-500"
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
            <label className="block text-sm font-medium text-gray-300">Preferred Language</label>
            <div className="mt-1 relative rounded-md shadow-sm">
              <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <Code className="h-5 w-5 text-gray-500" />
              </div>
              <select
                name="preferredLanguage" value={formData.preferredLanguage} onChange={handleChange}
                className="block w-full pl-10 pr-3 py-2 bg-[#0f1117] border border-[#30363d] rounded-md text-white focus:ring-brand-500 focus:border-brand-500"
              >
                <option value="JAVA">Java</option>
                <option value="PYTHON">Python</option>
                <option value="CPP">C++</option>
                <option value="JAVASCRIPT">JavaScript</option>
              </select>
            </div>
          </div>

          <div className="sm:col-span-2">
            <label className="block text-sm font-medium text-gray-300">Skills (comma separated)</label>
            <input
              name="skills" type="text" value={formData.skills} onChange={handleChange}
              className="mt-1 block w-full px-3 py-2 bg-[#0f1117] border border-[#30363d] rounded-md text-white focus:ring-brand-500 focus:border-brand-500"
              placeholder="e.g. React, Spring Boot, MySQL"
            />
          </div>

        </div>

        <div>
          <button
            type="submit"
            disabled={isSubmitting}
            className="w-full flex justify-center py-2.5 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-brand-600 hover:bg-brand-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-offset-[#0f1117] focus:ring-brand-500 disabled:opacity-50 transition-colors"
          >
            {isSubmitting ? (
              <>
                <Loader2 className="animate-spin -ml-1 mr-2 h-5 w-5" />
                Creating account...
              </>
            ) : (
              'Sign up'
            )}
          </button>
        </div>
      </form>

      <div className="mt-6 text-center">
        <p className="text-sm text-gray-400">
          Already have an account?{' '}
          <Link to="/login" className="font-medium text-brand-500 hover:text-brand-400">
            Sign in
          </Link>
        </p>
      </div>
    </div>
  );
};

export default RegisterPage;
