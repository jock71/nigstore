package models

case class UsedItem(usageDate: String,
                    quantity: Int)

case class StorageEntry(
                       product: String,
                       arrivalDate: String,
                       expireDate: String,
                       lot: String,
                       initialQuantity: Int,
                       remainingQuantity: Int,
                       exits: List[UsedItem]
                       )