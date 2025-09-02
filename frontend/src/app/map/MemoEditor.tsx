"use client";
import { useState } from "react";

import { number, z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { useFieldArray, useForm } from "react-hook-form";
import { TravelMemoFormSchema } from "@/schemas/FormSchemas";
import { Button } from "@/components/ui/button";
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";

import {
  HoverCard,
  HoverCardContent,
  HoverCardTrigger,
} from "@/components/ui/hover-card";

// Simple interface matching backend TravelMemoDto
interface SimpleTravelMemo {
  title: string;
  content: string;
  imageUrl: string;
  locations: SimpleLocation[];
  rating: number;
  moodIcon: string;
  tags: SimpleTag[];
  date: string; // ISO string for LocalDateTime
}

interface SimpleLocation {
  placeName: string;
  city: string;
  region: string;
  country: string;
  latitude?: number;
  longitude?: number;
}

interface SimpleTag {
  icon: string;
  tag: string;
}

interface MemoEditorProps {
  onSubmit: (memo: TravelMemoFormValues) => void;
  onCancel: () => void;
}
type TravelMemoFormValues = z.infer<typeof TravelMemoFormSchema>;

export default function MemoEditor({ onSubmit, onCancel }: MemoEditorProps) {
  const form = useForm({
    resolver: zodResolver(TravelMemoFormSchema),
    defaultValues: {
      title: "",
      content: "",
      imageUrl: "",
      location: [],
      rating: 0,
      moodIcon: "😊",
      tags: [],
      date: new Date().toISOString(),
    },
  });

  const [preview, setPreview] = useState<string>("");
  const locationFieldArray = useFieldArray({
    control: form.control,
    name: "location",
  });

  const tagFieldArray = useFieldArray({
    control: form.control,
    name: "tags",
  });

  return (
    <div className="overflow-y-auto max-h-96 md:max-h-[calc(100vh-12rem)] scrollbar-thin scrollbar-thumb-white/30 scrollbar-track-transparent">
      <div className="p-4 md:p-6">
        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
            {/* Title */}
            <FormField
              control={form.control}
              name="title"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Title</FormLabel>
                  <FormControl>
                    <Input placeholder="Paris Day Trip" {...field} />
                  </FormControl>
                </FormItem>
              )}
            />
            {/* Content */}
            <FormField
              control={form.control}
              name="content"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Content</FormLabel>
                  <FormControl>
                    <textarea
                      {...field}
                      rows={5}
                      placeholder="Write about your adventure..."
                      className="w-full px-4 py-3 bg-white/10 border border-white/20 rounded-xl text-white placeholder-white/50 focus:bg-white/15 focus:border-white/40 focus:outline-none transition-all duration-300 resize-y"
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <div className="flex w-full items-center">
              {/* Picture */}
              <FormField
                control={form.control}
                name="imageUrl"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Picture</FormLabel>
                    <FormControl>
                      <Input
                        type="file"
                        onChange={(e: React.ChangeEvent<HTMLInputElement>) => {
                          if (e.target.files && e.target.files[0]) {
                            // Update the field value with the selected file
                            field.onChange(e.target.files[0]);
                            setPreview(URL.createObjectURL(e.target.files[0]));
                          }
                        }}
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <HoverCard>
                <HoverCardTrigger asChild>
                  {preview ? (
                    <Button variant="link">Preview</Button>
                  ) : (
                    <Button variant="ghost" disabled>
                      No Preview
                    </Button>
                  )}
                </HoverCardTrigger>
                <HoverCardContent>
                  {preview ? (
                    <img src={preview} alt="Preview" className="w-full" />
                  ) : (
                    <p className="text-center">No preview available</p>
                  )}
                </HoverCardContent>
              </HoverCard>
            </div>
            {/* Location List */}
            <div className="space-y-2">
              <FormLabel>Locations</FormLabel>
              {locationFieldArray.fields.map((field, index) => (
                <div key={field.id} className="flex items-center space-x-2">
                  <FormField
                    control={form.control}
                    name={`location.${index}.placeName`}
                    render={({ field }) => (
                      <FormItem className="flex-1">
                        <FormControl>
                          <Input
                            placeholder="Paris, Île-de-France, France"
                            {...field}
                          />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                  <button
                    type="button"
                    onClick={() => locationFieldArray.remove(index)}
                    className="text-red-400"
                  >
                    ✕
                  </button>
                </div>
              ))}

              <button
                type="button"
                onClick={() =>
                  locationFieldArray.append({
                    placeName: "",
                    city: "",
                    region: "",
                    country: "",
                  })
                }
                className="px-3 py-2 bg-blue-500/20 rounded-lg"
              >
                + Add
              </button>
            </div>
            <div className="grid grid-cols-2 gap-3">
              {/* Rating */}
              <FormField
                control={form.control}
                name="rating"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Rating</FormLabel>
                    <FormControl>
                      <select
                        {...field}
                        className="w-full px-4 py-3 bg-white/10 rounded-xl text-white"
                      >
                        {[0, 1, 2, 3, 4, 5].map((r) => (
                          <option key={r} value={r}>
                            ⭐ {r}
                          </option>
                        ))}
                      </select>
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              {/* Mood */}
              <FormField
                control={form.control}
                name="moodIcon"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Mood</FormLabel>
                    <FormControl>
                      <select
                        {...field}
                        className="w-full px-4 py-3 bg-white/10 rounded-xl text-white"
                      >
                        <option value="😊">😊 Happy</option>
                        <option value="😍">😍 Amazed</option>
                        <option value="🤔">🤔 Thoughtful</option>
                        <option value="😌">😌 Peaceful</option>
                        <option value="🥳">🥳 Excited</option>
                        <option value="🤩">🤩 Starstruck</option>
                      </select>
                    </FormControl>
                  </FormItem>
                )}
              />
            </div>
            {/* Tags */}
            <div className="space-y-2">
              <FormLabel>Tags</FormLabel>
              {tagFieldArray.fields.map((field, index) => (
                <div key={field.id} className="flex items-center space-x-2">
                  <FormField
                    control={form.control}
                    name={`tags.${index}.tag`}
                    render={({ field }) => (
                      <FormItem className="flex-1">
                        <FormControl>
                          <Input placeholder="Add a tag..." {...field} />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                  <button
                    type="button"
                    onClick={() => tagFieldArray.remove(index)}
                    className="text-red-400"
                  >
                    ✕
                  </button>
                </div>
              ))}

              <button
                type="button"
                onClick={() =>
                  tagFieldArray.append({ description: "🏷️", tag: "" })
                }
                className="px-3 py-2 bg-blue-500/20 rounded-lg"
              >
                + Add Tag
              </button>
            </div>
            {/* Buttons */}
            <div className="flex space-x-3 pt-4">
              {/* <button
              type="submit"
              disabled={isSubmitting}
              className="flex-1 py-3 px-6 bg-blue-500/80 hover:bg-blue-500 border border-blue-400/50 text-white font-medium rounded-xl transition-all duration-300 disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center space-x-2"
            >
              {isSubmitting ? (
                <>
                  <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin"></div>
                  <span>Saving...</span>
                </>
              ) : (
                <>
                  <span>✈️</span>
                  <span>Save Journey</span>
                </>
              )}
            </button> */}

              <button
                type="submit"
                className="py-3 px-6 bg-blue-500 text-white rounded-xl"
              >
                Save Journey
              </button>

              <button
                type="button"
                onClick={onCancel}
                className="py-3 px-6 bg-white/10 hover:bg-white/20 border border-white/20 text-white/80 hover:text-white font-medium rounded-xl transition-all duration-300"
              >
                Cancel
              </button>
            </div>
          </form>
        </Form>
      </div>
    </div>
  );
}
