"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { authApi } from "../../lib/api";

export default function RegisterPage() {
  const router = useRouter();
  const [form, setForm] = useState({
    username: "",
    firstName: "",
    lastName: "",
    email: "",
    password: "",
    phoneNumber: "",
    profileImage: "",
    country: "",
    city: "",
  });
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setSuccess("");
    setIsLoading(true);
    try {
      await authApi.register(form);
      setSuccess("User registered successfully!");
      setTimeout(() => router.push("/admin-dashboard"), 1500);
    } catch (err) {
      setError(err.message || "Registration failed.");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-900">
      <form
        onSubmit={handleSubmit}
        className="bg-gray-800 p-8 rounded-lg shadow-lg w-full max-w-md"
      >
        <h2 className="text-2xl font-bold mb-6 text-gray-100">Register New User</h2>
        {error && <div className="text-red-500 mb-4">{error}</div>}
        {success && <div className="text-green-500 mb-4">{success}</div>}
        <input name="username" placeholder="Username" required className="input" value={form.username} onChange={handleChange} />
        <input name="firstName" placeholder="First Name" required className="input" value={form.firstName} onChange={handleChange} />
        <input name="lastName" placeholder="Last Name" required className="input" value={form.lastName} onChange={handleChange} />
        <input name="email" type="email" placeholder="Email" required className="input" value={form.email} onChange={handleChange} />
        <input name="password" type="password" placeholder="Password" required className="input" value={form.password} onChange={handleChange} />
        <input name="phoneNumber" placeholder="Phone Number (optional)" className="input" value={form.phoneNumber} onChange={handleChange} />
        <input name="profileImage" placeholder="Profile Image URL (optional)" className="input" value={form.profileImage} onChange={handleChange} />
        <input name="country" placeholder="Country (optional)" className="input" value={form.country} onChange={handleChange} />
        <input name="city" placeholder="City (optional)" className="input" value={form.city} onChange={handleChange} />
        <button
          type="submit"
          className="w-full bg-blue-600 hover:bg-blue-700 text-white py-3 rounded-lg font-semibold transition mt-6"
          disabled={isLoading}
        >
          {isLoading ? "Registering..." : "Register"}
        </button>
      </form>
      <style jsx>{`
        .input {
          width: 100%;
          margin-bottom: 1rem;
          padding: 0.75rem;
          border-radius: 0.5rem;
          border: 1px solid #374151;
          background: #1f2937;
          color: #f3f4f6;
        }
        .input:focus {
          outline: none;
          border-color: #2563eb;
        }
      `}</style>
    </div>
  );
}
