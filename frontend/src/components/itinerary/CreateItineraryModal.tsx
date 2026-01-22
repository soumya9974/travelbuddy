import { useState, FormEvent } from "react";
import { useAuth } from "../../context/AuthContext";
import { apiRequest } from "../../utils/api";
import { Modal } from "../ui/Modal";
import { Input } from "../ui/Input";
import { Button } from "../ui/Button";

interface CreateItineraryModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSuccess: () => void;
  groupId: number;
}

export const CreateItineraryModal = ({
  isOpen,
  onClose,
  onSuccess,
  groupId,
}: CreateItineraryModalProps) => {
  const { token } = useAuth();
  const [formData, setFormData] = useState({
    day: 1,
    title: "",
    description: "",
    location: "",
    startTime: "",
    endTime: "",
  });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError("");
    setLoading(true);

    try {
      await apiRequest(`/groups/${groupId}/itineraries`, {
        method: "POST",
        body: JSON.stringify({
          ...formData,
          groupId,
        }),
        token: token!,
      });

      setFormData({
        day: 1,
        title: "",
        description: "",
        location: "",
        startTime: "",
        endTime: "",
      });
      onSuccess();
    } catch (err) {
      setError(
        err instanceof Error ? err.message : "Failed to create itinerary"
      );
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (field: string, value: string | number) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title="Add Itinerary Activity"
      footer={
        <>
          <Button variant="secondary" onClick={onClose}>
            Cancel
          </Button>
          <Button onClick={handleSubmit} disabled={loading}>
            {loading ? "Adding..." : "Add Activity"}
          </Button>
        </>
      }
    >
      <form onSubmit={handleSubmit} className="space-y-4">
        <Input
          label="Day"
          type="number"
          min="1"
          value={formData.day}
          onChange={(e) => handleChange("day", parseInt(e.target.value))}
          required
        />

        <Input
          label="Activity Title"
          type="text"
          value={formData.title}
          onChange={(e) => handleChange("title", e.target.value)}
          placeholder="e.g., Visit Eiffel Tower"
          required
        />

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Description
          </label>
          <textarea
            value={formData.description}
            onChange={(e) => handleChange("description", e.target.value)}
            placeholder="Describe the activity..."
            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all resize-none"
            rows={3}
            required
          />
        </div>

        <Input
          label="Location"
          type="text"
          value={formData.location}
          onChange={(e) => handleChange("location", e.target.value)}
          placeholder="e.g., Champ de Mars, Paris"
          required
        />

        <div className="grid grid-cols-2 gap-4">
          <Input
            label="Start Time (Optional)"
            type="time"
            value={formData.startTime}
            onChange={(e) => handleChange("startTime", e.target.value)}
          />

          <Input
            label="End Time (Optional)"
            type="time"
            value={formData.endTime}
            onChange={(e) => handleChange("endTime", e.target.value)}
          />
        </div>

        {error && (
          <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg text-sm">
            {error}
          </div>
        )}
      </form>
    </Modal>
  );
};
