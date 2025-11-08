import { z } from 'zod'

export const dateArraySchema = z
  .array(z.number())
  .length(6)
  .transform((dateParts) => {
    const [year, month, day, hour, minute, second] = dateParts

    const monthIndex = (month as number) - 1

    return new Date(year as number, monthIndex, day, hour, minute, second)
  })

export const errorSchema = z.object({
  error: z.string(),
})
