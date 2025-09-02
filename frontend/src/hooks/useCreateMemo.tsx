import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useApiClient } from "./useApiClient";
import axios from "axios";
import { TravelMemoFormSchema } from "@/schemas/FormSchemas";
import { z } from "zod";
import { toast } from "sonner";

export const useCreateMemo = () => {
  const apiClient = useApiClient();
  const queryClient = useQueryClient();

  type TravelMemoFormValues = z.infer<typeof TravelMemoFormSchema>;
  const uploadImage = async (file: File) => {
    const formData = new FormData();
    formData.append("file", file);
    formData.append("upload_preset", "recipe_upload");

    try {
      // Note: Later we can use apiClient to make private requests
      const response = await axios.post(
        `https://api.cloudinary.com/v1_1/${process.env.NEXT_PUBLIC_CLOUDINARY_CLOUD_NAME}/image/upload`,
        formData
      );
      return response.data.secure_url;
    } catch (error) {
      console.error("Error uploading image: ", error);
      throw error;
    }
  };
  const mutation = useMutation({
    mutationFn: async (data: TravelMemoFormValues) => {
      // console.log("Form submitted", data);
      try {
        if (data.imageUrl instanceof File) {
          const imageUrl = await uploadImage(data.imageUrl);
          console.log(data.imageUrl);
          const memoForm = { ...data, imageUrl };
          await apiClient.post("/api/memos", memoForm);
        }
      } catch (error) {
        console.error("Error in form submission", error);
      }
    },
    onSuccess: () => {
      // Note: Later need to invalidate other queries based on user's role
      queryClient.invalidateQueries({ queryKey: ["myMemos"] });

      toast.success("New Memo posted, pending for review");
    },
    onError: (error) => {
      console.error("Error in form submission", error);
      toast.error("Error in form submission");
    },
  });
  return {
    uploadAsync: mutation.mutateAsync,
    isLoadingNew: mutation.isPending,
    isErrorNew: mutation.isError,
    isSuccessNew: mutation.isSuccess,
  };
};
