import { z } from "zod";

export const TagSchema = z.object({
  description: z.string().min(1, "Description required"),
  tag: z.string().min(1, "Tag name required"),
});

export const LocationSchema = z.object({
  placeName: z.string().optional(),
  city: z.string().min(1, "City is required"),
  region: z.string().min(1, "Region is required"),
  country: z.string().min(1, "Country is required"),
});

export const TravelMemoFormSchema = z.object({
  title: z.string().min(1, "Title is required").max(100, "Title too long"),
  content: z.string().min(1, "Content is required"),
  imageUrl: z.union([
    z.string().url("Image must be a valid URL"),
    z.instanceof(File), // 👈 allow File uploads
  ]),
  location: z.array(LocationSchema).nonempty("At least one location required"),

  rating: z.number().min(0).max(5),
  moodIcon: z.string().optional(),

  tags: z.array(TagSchema).default([]),

  date: z.string().refine((val) => !isNaN(Date.parse(val)), {
    message: "Invalid date format",
  }),
});

// Type inference
export type TravelMemoForm = z.infer<typeof TravelMemoFormSchema>;
