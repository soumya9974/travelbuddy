import { useState, useEffect } from "react";
import { useAuth } from "../context/AuthContext";
import { TravelGroup } from "../types";
import { apiRequest } from "../utils/api";
import { Card, CardBody, CardHeader } from "../components/ui/Card";
import { Button } from "../components/ui/Button";
import { Input } from "../components/ui/Input";
import { Spinner } from "../components/ui/Spinner";
import { Plus, MapPin, Calendar, Users, Search } from "lucide-react";
import { CreateGroupModal } from "../components/groups/CreateGroupModal";
import { GroupCard } from "../components/groups/GroupCard";

interface GroupsProps {
  onSelectGroup: (group: TravelGroup) => void;
}

export const Groups = ({ onSelectGroup }: GroupsProps) => {
  const { token } = useAuth();
  const [groups, setGroups] = useState<TravelGroup[]>([]);
  const [filteredGroups, setFilteredGroups] = useState<TravelGroup[]>([]);
  const [searchQuery, setSearchQuery] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [showCreateModal, setShowCreateModal] = useState(false);

  const fetchGroups = async () => {
    try {
      const data = await apiRequest("/groups", { token: token! });
      setGroups(data);
      setFilteredGroups(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to fetch groups");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchGroups();
  }, [token]);

  useEffect(() => {
    if (!searchQuery.trim()) {
      setFilteredGroups(groups);
      return;
    }

    const query = searchQuery.toLowerCase();
    const filtered = groups.filter(
      (group) =>
        group.name.toLowerCase().includes(query) ||
        group.destination.toLowerCase().includes(query) ||
        (group.interest && group.interest.toLowerCase().includes(query)) ||
        (group.description && group.description.toLowerCase().includes(query))
    );
    setFilteredGroups(filtered);
  }, [searchQuery, groups]);

  const handleCreateGroup = () => {
    setShowCreateModal(false);
    fetchGroups();
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-96">
        <Spinner size="lg" />
      </div>
    );
  }

  return (
    <div className="relative min-h-screen pb-12">
      {/* ✅ BACKGROUND IMAGE LAYER */}
      <div
        className="fixed inset-0 -z-10 bg-cover bg-center"
        style={{
          backgroundImage:
            "url('https://images.pexels.com/photos/1007657/pexels-photo-1007657.jpeg?auto=compress&cs=tinysrgb&w=1920')",
        }}
      >
        {/* overlay for readability */}
        <div className="absolute inset-0 bg-white/70" />
      </div>
      {/* ✅ PAGE CONTENT */}
      <div className="bg-gradient-to-r from-blue-600 via-cyan-600 to-teal-600 rounded-2xl p-8 mb-8 shadow-xl">
        <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
          <div>
            <h1 className="text-3xl md:text-4xl font-bold text-white mb-2">
              Travel Groups
            </h1>
            <p className="text-blue-50 text-lg">
              Explore and join exciting travel adventures
            </p>
          </div>
          <Button
            onClick={() => setShowCreateModal(true)}
            className="bg-white !text-blue-600 hover:bg-gray-50 shadow-lg whitespace-nowrap"
          >
            <Plus size={20} className="mr-2" />
            Create Group
          </Button>
        </div>
      </div>

      <div className="mb-6">
        <div className="relative max-w-md">
          <Search
            className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400"
            size={20}
          />
          <input
            type="text"
            placeholder="Search by name, destination, or interest..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent bg-white shadow-sm transition-shadow hover:shadow-md"
          />
        </div>
      </div>

      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg mb-6 whitespace-pre-line">
          {error}
        </div>
      )}

      {filteredGroups.length === 0 ? (
        <Card>
          <CardBody className="text-center py-12">
            {searchQuery ? (
              <>
                <Search size={48} className="mx-auto text-gray-400 mb-4" />
                <h3 className="text-xl font-semibold text-gray-900 mb-2">
                  No groups found
                </h3>
                <p className="text-gray-600 mb-4">
                  Try adjusting your search criteria
                </p>
                <Button variant="secondary" onClick={() => setSearchQuery("")}>
                  Clear Search
                </Button>
              </>
            ) : (
              <>
                <Users size={48} className="mx-auto text-gray-400 mb-4" />
                <h3 className="text-xl font-semibold text-gray-900 mb-2">
                  No groups yet
                </h3>
                <p className="text-gray-600 mb-4">
                  Create your first travel group to get started!
                </p>
                <Button onClick={() => setShowCreateModal(true)}>
                  <Plus size={20} className="mr-2" />
                  Create Your First Group
                </Button>
              </>
            )}
          </CardBody>
        </Card>
      ) : (
        <>
          {searchQuery && (
            <div className="mb-4 text-sm text-gray-700 font-medium">
              Found {filteredGroups.length}{" "}
              {filteredGroups.length === 1 ? "group" : "groups"}
            </div>
          )}
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {filteredGroups.map((group) => (
              <GroupCard
                key={group.id}
                group={group}
                onClick={() => onSelectGroup(group)}
              />
            ))}
          </div>
        </>
      )}

      <CreateGroupModal
        isOpen={showCreateModal}
        onClose={() => setShowCreateModal(false)}
        onSuccess={handleCreateGroup}
      />
    </div>
  );
};
